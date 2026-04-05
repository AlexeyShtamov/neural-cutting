package ru.shtamov.neural_cutting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisJobResponse;
import ru.shtamov.neural_cutting.dto.analysis.AnalysisResultResponse;
import ru.shtamov.neural_cutting.dto.analysis.CreateAnalysisJobRequest;
import ru.shtamov.neural_cutting.security.AuthenticatedUser;
import ru.shtamov.neural_cutting.service.AnalysisJobService;

import java.util.UUID;

@RestController
@RequestMapping("/api/analysis-jobs")
@Tag(name = "Analysis", description = "Resume analysis jobs and results")
public class AnalysisJobController {

    private final AnalysisJobService analysisJobService;

    public AnalysisJobController(AnalysisJobService analysisJobService) {
        this.analysisJobService = analysisJobService;
    }

    @PostMapping
    @Operation(summary = "Create and run analysis job for a resume version and vacancy")
    public ResponseEntity<AnalysisJobResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateAnalysisJobRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(analysisJobService.createAndRun(user.id(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get analysis job status")
    public AnalysisJobResponse getById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id
    ) {
        return analysisJobService.getJob(user.id(), id);
    }

    @GetMapping("/{id}/result")
    @Operation(summary = "Get completed analysis result")
    public AnalysisResultResponse getResult(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id
    ) {
        return analysisJobService.getResult(user.id(), id);
    }
}
