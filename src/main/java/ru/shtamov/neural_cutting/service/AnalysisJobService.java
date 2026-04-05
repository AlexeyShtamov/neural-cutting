package ru.shtamov.neural_cutting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.shtamov.neural_cutting.domain.AnalysisJob;
import ru.shtamov.neural_cutting.domain.AnalysisResult;
import ru.shtamov.neural_cutting.domain.ResumeVersion;
import ru.shtamov.neural_cutting.domain.Vacancy;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisHistoryItemResponse;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisJobResponse;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisResultResponse;
import ru.shtamov.neural_cutting.dto.analysis.CreateAnalysisJobRequest;
import ru.shtamov.neural_cutting.dto.common.PageResponse;
import ru.shtamov.neural_cutting.exception.ConflictException;
import ru.shtamov.neural_cutting.exception.ExternalServiceException;
import ru.shtamov.neural_cutting.exception.NotFoundException;
import ru.shtamov.neural_cutting.integration.analysis.AnalysisClient;
import ru.shtamov.neural_cutting.integration.analysis.ExternalAnalysisRequest;
import ru.shtamov.neural_cutting.integration.analysis.ExternalAnalysisResponse;
import ru.shtamov.neural_cutting.mapper.AnalysisMapper;
import ru.shtamov.neural_cutting.repository.AnalysisJobRepository;

import java.util.UUID;

@Service
@Slf4j
public class AnalysisJobService {

    private final ResumeVersionService resumeVersionService;
    private final VacancyService vacancyService;
    private final AnalysisJobStateService analysisJobStateService;
    private final AnalysisJobRepository analysisJobRepository;
    private final AnalysisMapper analysisMapper;
    private final AnalysisClient analysisClient;

    public AnalysisJobService(
            ResumeVersionService resumeVersionService,
            VacancyService vacancyService,
            AnalysisJobStateService analysisJobStateService,
            AnalysisJobRepository analysisJobRepository,
            AnalysisMapper analysisMapper,
            AnalysisClient analysisClient
    ) {
        this.resumeVersionService = resumeVersionService;
        this.vacancyService = vacancyService;
        this.analysisJobStateService = analysisJobStateService;
        this.analysisJobRepository = analysisJobRepository;
        this.analysisMapper = analysisMapper;
        this.analysisClient = analysisClient;
    }

    public AnalysisJobResponse createAndRun(UUID ownerId, CreateAnalysisJobRequest request) {
        ResumeVersion resumeVersion = resumeVersionService.getOwnedVersion(ownerId, request.resumeVersionId());
        Vacancy vacancy = vacancyService.getOwnedVacancy(ownerId, request.vacancyId());

        AnalysisJob createdJob = analysisJobStateService.createJob(resumeVersion, vacancy);
        analysisJobStateService.markRunning(createdJob.getId());
        log.info("Started analysis job id={} for resumeVersion={} vacancy={} owner={}",
                createdJob.getId(), resumeVersion.getId(), vacancy.getId(), ownerId);

        try {
            ExternalAnalysisRequest externalRequest = toExternalRequest(resumeVersion, vacancy);
            ExternalAnalysisResponse externalResponse = analysisClient.analyze(externalRequest);
            analysisJobStateService.markSuccess(createdJob.getId(), externalResponse);
            log.info("Completed analysis job id={} with status=SUCCESS", createdJob.getId());
            return analysisMapper.toJobResponse(getDetailedJobOrThrow(createdJob.getId(), ownerId));
        } catch (ExternalServiceException exception) {
            analysisJobStateService.markFailed(
                    createdJob.getId(),
                    502,
                    sanitizeErrorMessage(exception.getMessage())
            );
            log.warn("Analysis job id={} failed due to external service error: {}", createdJob.getId(), exception.getMessage());
            return analysisMapper.toJobResponse(getDetailedJobOrThrow(createdJob.getId(), ownerId));
        } catch (RuntimeException exception) {
            analysisJobStateService.markFailed(
                    createdJob.getId(),
                    500,
                    sanitizeErrorMessage(exception.getMessage())
            );
            log.error("Analysis job id={} failed unexpectedly", createdJob.getId(), exception);
            return analysisMapper.toJobResponse(getDetailedJobOrThrow(createdJob.getId(), ownerId));
        }
    }

    @Transactional(readOnly = true)
    public AnalysisJobResponse getJob(UUID ownerId, UUID jobId) {
        AnalysisJob job = analysisJobRepository.findDetailedByIdAndResumeVersionResumeOwnerId(jobId, ownerId)
                .orElseThrow(() -> new NotFoundException("Analysis job not found"));
        return analysisMapper.toJobResponse(job);
    }

    @Transactional(readOnly = true)
    public AnalysisResultResponse getResult(UUID ownerId, UUID jobId) {
        AnalysisJob job = analysisJobRepository.findWithResultByIdAndResumeVersionResumeOwnerId(jobId, ownerId)
                .orElseThrow(() -> new NotFoundException("Analysis job not found"));

        AnalysisResult result = job.getAnalysisResult();
        if (result == null) {
            throw new ConflictException("Analysis result is not available for this job yet");
        }
        return analysisMapper.toResultResponse(result);
    }

    @Transactional(readOnly = true)
    public PageResponse<AnalysisHistoryItemResponse> getHistory(UUID ownerId, UUID resumeVersionId, Pageable pageable) {
        resumeVersionService.getOwnedVersion(ownerId, resumeVersionId);
        Page<AnalysisHistoryItemResponse> page = analysisJobRepository
                .findAllByResumeVersionIdAndResumeVersionResumeOwnerId(resumeVersionId, ownerId, pageable)
                .map(analysisMapper::toHistoryItem);
        return PageResponse.from(page);
    }

    private ExternalAnalysisRequest toExternalRequest(ResumeVersion resumeVersion, Vacancy vacancy) {
        return new ExternalAnalysisRequest(
                new ExternalAnalysisRequest.ResumePayload(
                        resumeVersion.getId(),
                        resumeVersion.getResume().getTitle(),
                        resumeVersion.getTextContent(),
                        resumeVersion.getSourceType(),
                        resumeVersion.getOriginalFileName(),
                        resumeVersion.getStoragePath()
                ),
                new ExternalAnalysisRequest.VacancyPayload(
                        vacancy.getId(),
                        vacancy.getTitle(),
                        vacancy.getCompany(),
                        vacancy.getText()
                )
        );
    }

    private String sanitizeErrorMessage(String message) {
        if (!StringUtils.hasText(message)) {
            return "Analysis processing failed";
        }
        return message.length() <= 500 ? message : message.substring(0, 500);
    }

    private AnalysisJob getDetailedJobOrThrow(UUID jobId, UUID ownerId) {
        return analysisJobRepository.findDetailedByIdAndResumeVersionResumeOwnerId(jobId, ownerId)
                .orElseThrow(() -> new NotFoundException("Analysis job not found"));
    }
}
