package ru.shtamov.neural_cutting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import ru.shtamov.neural_cutting.dto.common.PageResponse;
import ru.shtamov.neural_cutting.dto.resume.CreateResumeRequest;
import ru.shtamov.neural_cutting.dto.resume.CreateResumeVersionTextRequest;
import ru.shtamov.neural_cutting.dto.resume.ResumeDetailResponse;
import ru.shtamov.neural_cutting.dto.resume.ResumeResponse;
import ru.shtamov.neural_cutting.dto.resume.ResumeVersionResponse;
import ru.shtamov.neural_cutting.dto.resume.UpdateResumeRequest;
import ru.shtamov.neural_cutting.security.AuthenticatedUser;
import ru.shtamov.neural_cutting.service.ResumeService;
import ru.shtamov.neural_cutting.service.ResumeVersionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resumes", description = "Resume and resume version management")
public class ResumeController {

    private final ResumeService resumeService;
    private final ResumeVersionService resumeVersionService;

    public ResumeController(ResumeService resumeService, ResumeVersionService resumeVersionService) {
        this.resumeService = resumeService;
        this.resumeVersionService = resumeVersionService;
    }

    @PostMapping
    @Operation(summary = "Create a new resume")
    public ResponseEntity<ResumeResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateResumeRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resumeService.create(user.id(), request));
    }

    @GetMapping
    @Operation(summary = "Get paginated list of current user's resumes")
    public PageResponse<ResumeResponse> getAll(
            @AuthenticationPrincipal AuthenticatedUser user,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return resumeService.getResumes(user.id(), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resume details")
    public ResumeDetailResponse getById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id
    ) {
        return resumeService.getResume(user.id(), id);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update resume metadata")
    public ResumeResponse update(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateResumeRequest request
    ) {
        return resumeService.update(user.id(), id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume and all nested versions/analysis data")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id
    ) {
        resumeService.delete(user.id(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/versions/text")
    @Operation(summary = "Create a new resume version from plain text")
    public ResponseEntity<ResumeVersionResponse> createTextVersion(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @Valid @RequestBody CreateResumeVersionTextRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resumeVersionService.createTextVersion(user.id(), id, request));
    }

    @PostMapping("/{id}/versions/upload")
    @Operation(summary = "Upload PDF/DOC/DOCX as a new resume version")
    public ResponseEntity<ResumeVersionResponse> uploadVersion(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resumeVersionService.uploadVersion(user.id(), id, file));
    }

    @GetMapping("/{id}/versions")
    @Operation(summary = "List resume versions")
    public List<ResumeVersionResponse> getVersions(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id
    ) {
        return resumeVersionService.getVersions(user.id(), id);
    }
}
