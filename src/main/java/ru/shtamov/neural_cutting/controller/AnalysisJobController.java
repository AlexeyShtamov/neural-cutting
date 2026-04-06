package ru.shtamov.neural_cutting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "🤖 Анализ", description = "Анализ соответствия резюме вакансии")
public class AnalysisJobController {

    private final AnalysisJobService analysisJobService;

    public AnalysisJobController(AnalysisJobService analysisJobService) {
        this.analysisJobService = analysisJobService;
    }

    @PostMapping
    @Operation(summary = "Запустить анализ",
               description = "Создаёт и запускает задачу анализа соответствия резюме вакансии. " +
                            "Возвращает статус CREATED или RUNNING с ID задачи.")
    public ResponseEntity<AnalysisJobResponse> create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateAnalysisJobRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(analysisJobService.createAndRun(user.id(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Статус задачи",
               description = "Возвращает текущий статус задачи анализа (CREATED, RUNNING, SUCCESS, FAILED)")
    public AnalysisJobResponse getById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "ID задачи анализа") @PathVariable UUID id
    ) {
        return analysisJobService.getJob(user.id(), id);
    }

    @GetMapping("/{id}/result")
    @Operation(summary = "Результат анализа",
               description = "Возвращает детальный результат анализа: score, проблемы, рекомендации, " +
                            "процент соответствия навыков, отсутствующие навыки")
    public AnalysisResultResponse getResult(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "ID задачи анализа") @PathVariable UUID id
    ) {
        return analysisJobService.getResult(user.id(), id);
    }
}
