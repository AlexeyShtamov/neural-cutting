package ru.shtamov.neural_cutting.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shtamov.neural_cutting.domain.AnalysisJob;
import ru.shtamov.neural_cutting.domain.AnalysisResult;
import ru.shtamov.neural_cutting.domain.Problem;
import ru.shtamov.neural_cutting.domain.Recommendation;
import ru.shtamov.neural_cutting.domain.ResumeVersion;
import ru.shtamov.neural_cutting.domain.Vacancy;
import ru.shtamov.neural_cutting.domain.enums.AnalysisJobStatus;
import ru.shtamov.neural_cutting.integration.analysis.ExternalAnalysisResponse;
import ru.shtamov.neural_cutting.repository.AnalysisJobRepository;
import ru.shtamov.neural_cutting.repository.AnalysisResultRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class AnalysisJobStateService {

    private final AnalysisJobRepository analysisJobRepository;
    private final AnalysisResultRepository analysisResultRepository;

    public AnalysisJobStateService(
            AnalysisJobRepository analysisJobRepository,
            AnalysisResultRepository analysisResultRepository
    ) {
        this.analysisJobRepository = analysisJobRepository;
        this.analysisResultRepository = analysisResultRepository;
    }

    @Transactional
    public AnalysisJob createJob(ResumeVersion resumeVersion, Vacancy vacancy) {
        AnalysisJob job = new AnalysisJob();
        job.setStatus(AnalysisJobStatus.CREATED);
        job.setResumeVersion(resumeVersion);
        job.setVacancy(vacancy);
        return analysisJobRepository.save(job);
    }

    @Transactional
    public AnalysisJob markRunning(UUID jobId) {
        AnalysisJob job = analysisJobRepository.getReferenceById(jobId);
        job.setStatus(AnalysisJobStatus.RUNNING);
        job.setStartedAt(Instant.now());
        job.setErrorCode(null);
        job.setErrorMessage(null);
        return job;
    }

    @Transactional
    public AnalysisJob markFailed(UUID jobId, int errorCode, String errorMessage) {
        AnalysisJob job = analysisJobRepository.getReferenceById(jobId);
        job.setStatus(AnalysisJobStatus.FAILED);
        job.setErrorCode(errorCode);
        job.setErrorMessage(errorMessage);
        job.setFinishedAt(Instant.now());
        return job;
    }

    @Transactional
    public AnalysisJob markSuccess(UUID jobId, ExternalAnalysisResponse response) {
        AnalysisJob job = analysisJobRepository.getReferenceById(jobId);
        AnalysisResult result = new AnalysisResult();
        result.setAnalysisJob(job);
        result.setScore(response.score());
        result.setGradeLabel(response.gradeLabel());
        result.setSummary(response.summary());
        result.setOverallFitPercent(response.overallFitPercent());

        response.problems().forEach(problemData -> {
            Problem problem = new Problem();
            problem.setAnalysisResult(result);
            problem.setCategory(problemData.category());
            problem.setSeverity(problemData.severity());
            problem.setFragment(problemData.fragment());
            problem.setDescription(problemData.description());
            result.getProblems().add(problem);
        });

        response.recommendations().forEach(recommendationData -> {
            Recommendation recommendation = new Recommendation();
            recommendation.setAnalysisResult(result);
            recommendation.setAction(recommendationData.action());
            recommendation.setExample(recommendationData.example());
            recommendation.setPriority(recommendationData.priority());
            result.getRecommendations().add(recommendation);
        });

        AnalysisResult savedResult = analysisResultRepository.save(result);
        job.setStatus(AnalysisJobStatus.SUCCESS);
        job.setFinishedAt(Instant.now());
        job.setErrorCode(null);
        job.setErrorMessage(null);
        job.setAnalysisResult(savedResult);
        return job;
    }
}
