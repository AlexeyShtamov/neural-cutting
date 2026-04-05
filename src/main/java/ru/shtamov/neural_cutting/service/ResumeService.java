package ru.shtamov.neural_cutting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.shtamov.neural_cutting.domain.Person;
import ru.shtamov.neural_cutting.domain.Resume;
import ru.shtamov.neural_cutting.dto.common.PageResponse;
import ru.shtamov.neural_cutting.dto.resume.CreateResumeRequest;
import ru.shtamov.neural_cutting.dto.resume.ResumeDetailResponse;
import ru.shtamov.neural_cutting.dto.resume.ResumeResponse;
import ru.shtamov.neural_cutting.dto.resume.ResumeVersionResponse;
import ru.shtamov.neural_cutting.dto.resume.UpdateResumeRequest;
import ru.shtamov.neural_cutting.exception.BadRequestException;
import ru.shtamov.neural_cutting.exception.NotFoundException;
import ru.shtamov.neural_cutting.mapper.ResumeMapper;
import ru.shtamov.neural_cutting.repository.PersonRepository;
import ru.shtamov.neural_cutting.repository.ResumeRepository;
import ru.shtamov.neural_cutting.storage.FileStorageService;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final PersonRepository personRepository;
    private final ResumeMapper resumeMapper;
    private final FileStorageService fileStorageService;

    public ResumeService(
            ResumeRepository resumeRepository,
            PersonRepository personRepository,
            ResumeMapper resumeMapper,
            FileStorageService fileStorageService
    ) {
        this.resumeRepository = resumeRepository;
        this.personRepository = personRepository;
        this.resumeMapper = resumeMapper;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public ResumeResponse create(UUID ownerId, CreateResumeRequest request) {
        Person owner = personRepository.getReferenceById(ownerId);
        Resume resume = new Resume();
        resume.setTitle(request.title().trim());
        resume.setLanguage(request.language());
        resume.setTargetRole(request.targetRole().trim());
        resume.setOwner(owner);
        Resume savedResume = resumeRepository.save(resume);
        log.info("Created resume id={} for owner={}", savedResume.getId(), ownerId);
        return resumeMapper.toResponse(savedResume);
    }

    @Transactional(readOnly = true)
    public PageResponse<ResumeResponse> getResumes(UUID ownerId, Pageable pageable) {
        Page<ResumeResponse> page = resumeRepository.findAllByOwnerId(ownerId, pageable)
                .map(resumeMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public ResumeDetailResponse getResume(UUID ownerId, UUID resumeId) {
        Resume resume = getOwnedResumeWithVersions(ownerId, resumeId);
        List<ResumeVersionResponse> versions = resume.getVersions().stream()
                .map(resumeMapper::toVersionResponse)
                .toList();
        return resumeMapper.toDetailResponse(resume, versions);
    }

    @Transactional
    public ResumeResponse update(UUID ownerId, UUID resumeId, UpdateResumeRequest request) {
        Resume resume = getOwnedResume(ownerId, resumeId);
        if (request.title() != null) {
            if (!StringUtils.hasText(request.title())) {
                throw new BadRequestException("Resume title must not be blank");
            }
            resume.setTitle(request.title().trim());
        }
        if (request.language() != null) {
            resume.setLanguage(request.language());
        }
        if (request.targetRole() != null) {
            if (!StringUtils.hasText(request.targetRole())) {
                throw new BadRequestException("Resume targetRole must not be blank");
            }
            resume.setTargetRole(request.targetRole().trim());
        }
        log.info("Updated resume id={} for owner={}", resumeId, ownerId);
        return resumeMapper.toResponse(resume);
    }

    @Transactional
    public void delete(UUID ownerId, UUID resumeId) {
        Resume resume = getOwnedResumeWithVersions(ownerId, resumeId);
        resume.getVersions().stream()
                .map(version -> version.getStoragePath())
                .forEach(fileStorageService::deleteIfExists);
        resumeRepository.delete(resume);
        log.info("Deleted resume id={} for owner={}", resumeId, ownerId);
    }

    @Transactional(readOnly = true)
    public Resume getOwnedResume(UUID ownerId, UUID resumeId) {
        return resumeRepository.findByIdAndOwnerId(resumeId, ownerId)
                .orElseThrow(() -> new NotFoundException("Resume not found"));
    }

    @Transactional(readOnly = true)
    public Resume getOwnedResumeWithVersions(UUID ownerId, UUID resumeId) {
        return resumeRepository.findWithVersionsByIdAndOwnerId(resumeId, ownerId)
                .orElseThrow(() -> new NotFoundException("Resume not found"));
    }
}
