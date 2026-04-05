package ru.shtamov.neural_cutting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.shtamov.neural_cutting.domain.Resume;
import ru.shtamov.neural_cutting.domain.ResumeVersion;
import ru.shtamov.neural_cutting.domain.enums.SourceType;
import ru.shtamov.neural_cutting.dto.resume.CreateResumeVersionTextRequest;
import ru.shtamov.neural_cutting.dto.resume.ResumeVersionResponse;
import ru.shtamov.neural_cutting.exception.BadRequestException;
import ru.shtamov.neural_cutting.exception.NotFoundException;
import ru.shtamov.neural_cutting.mapper.ResumeMapper;
import ru.shtamov.neural_cutting.repository.ResumeVersionRepository;
import ru.shtamov.neural_cutting.storage.FileStorageService;
import ru.shtamov.neural_cutting.storage.StoredFileDescriptor;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class ResumeVersionService {

    private final ResumeService resumeService;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeMapper resumeMapper;
    private final FileStorageService fileStorageService;

    public ResumeVersionService(
            ResumeService resumeService,
            ResumeVersionRepository resumeVersionRepository,
            ResumeMapper resumeMapper,
            FileStorageService fileStorageService
    ) {
        this.resumeService = resumeService;
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeMapper = resumeMapper;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public ResumeVersionResponse createTextVersion(UUID ownerId, UUID resumeId, CreateResumeVersionTextRequest request) {
        Resume resume = resumeService.getOwnedResume(ownerId, resumeId);
        ResumeVersion version = new ResumeVersion();
        version.setResume(resume);
        version.setVersionNumber(nextVersionNumber(resumeId));
        version.setTextContent(request.text().trim());
        version.setSourceType(SourceType.TEXT);
        ResumeVersion savedVersion = resumeVersionRepository.save(version);
        log.info("Created text resume version id={} for resume={} owner={}", savedVersion.getId(), resumeId, ownerId);
        return resumeMapper.toVersionResponse(savedVersion);
    }

    @Transactional
    public ResumeVersionResponse uploadVersion(UUID ownerId, UUID resumeId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file must not be empty");
        }

        Resume resume = resumeService.getOwnedResume(ownerId, resumeId);
        int versionNumber = nextVersionNumber(resumeId);
        SourceType sourceType = detectSourceType(file);
        StoredFileDescriptor storedFile = null;

        try {
            storedFile = fileStorageService.store(file, ownerId, resumeId, versionNumber);
            ResumeVersion version = new ResumeVersion();
            version.setResume(resume);
            version.setVersionNumber(versionNumber);
            version.setSourceType(sourceType);
            version.setStoragePath(storedFile.storagePath());
            version.setOriginalFileName(storedFile.originalFileName());
            version.setContentType(storedFile.contentType());
            version.setFileSize(storedFile.fileSize());
            ResumeVersion savedVersion = resumeVersionRepository.save(version);
            log.info("Uploaded file resume version id={} for resume={} owner={}", savedVersion.getId(), resumeId, ownerId);
            return resumeMapper.toVersionResponse(savedVersion);
        } catch (RuntimeException exception) {
            if (storedFile != null) {
                fileStorageService.deleteIfExists(storedFile.storagePath());
            }
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ResumeVersionResponse> getVersions(UUID ownerId, UUID resumeId) {
        resumeService.getOwnedResume(ownerId, resumeId);
        return resumeVersionRepository.findAllByResumeIdAndResumeOwnerIdOrderByVersionNumberDesc(resumeId, ownerId)
                .stream()
                .map(resumeMapper::toVersionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumeVersionResponse getVersion(UUID ownerId, UUID versionId) {
        return resumeMapper.toVersionResponse(getOwnedVersion(ownerId, versionId));
    }

    @Transactional(readOnly = true)
    public ResumeVersion getOwnedVersion(UUID ownerId, UUID versionId) {
        return resumeVersionRepository.findDetailedByIdAndResumeOwnerId(versionId, ownerId)
                .orElseThrow(() -> new NotFoundException("Resume version not found"));
    }

    private int nextVersionNumber(UUID resumeId) {
        return resumeVersionRepository.findMaxVersionNumberByResumeId(resumeId) + 1;
    }

    private SourceType detectSourceType(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            throw new BadRequestException("Uploaded file name is missing");
        }

        String normalizedFileName = fileName.toLowerCase(Locale.ROOT);
        if (normalizedFileName.endsWith(".pdf")) {
            return SourceType.PDF;
        }
        if (normalizedFileName.endsWith(".docx")) {
            return SourceType.DOCX;
        }
        if (normalizedFileName.endsWith(".doc")) {
            return SourceType.DOC;
        }
        throw new BadRequestException("Only PDF, DOC, and DOCX files are supported");
    }
}
