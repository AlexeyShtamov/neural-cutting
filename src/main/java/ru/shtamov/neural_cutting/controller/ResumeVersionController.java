package ru.shtamov.neural_cutting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisHistoryItemResponse;
import ru.shtamov.neural_cutting.dto.common.PageResponse;
import ru.shtamov.neural_cutting.dto.resume.ResumeVersionResponse;
import ru.shtamov.neural_cutting.security.AuthenticatedUser;
import ru.shtamov.neural_cutting.service.AnalysisJobService;
import ru.shtamov.neural_cutting.service.ResumeVersionService;

import java.util.UUID;

@RestController
@Tag(name = "Resume Versions", description = "Resume version details and analysis history")
public class ResumeVersionController {

    private final ResumeVersionService resumeVersionService;
    private final AnalysisJobService analysisJobService;

    public ResumeVersionController(ResumeVersionService resumeVersionService, AnalysisJobService analysisJobService) {
        this.resumeVersionService = resumeVersionService;
        this.analysisJobService = analysisJobService;
    }

    @GetMapping("/api/resume-versions/{versionId}")
    @Operation(summary = "Get resume version by id")
    public ResumeVersionResponse getVersion(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID versionId
    ) {
        return resumeVersionService.getVersion(user.id(), versionId);
    }

    @GetMapping("/api/resume-versions/{id}/analysis-history")
    @Operation(summary = "Get paginated analysis history for a resume version")
    public PageResponse<AnalysisHistoryItemResponse> getAnalysisHistory(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return analysisJobService.getHistory(user.id(), id, pageable);
    }
}
