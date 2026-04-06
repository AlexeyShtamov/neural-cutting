package ru.shtamov.neural_cutting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shtamov.neural_cutting.dto.common.PageResponse;
import ru.shtamov.neural_cutting.dto.vacancy.CreateManualVacancyRequest;
import ru.shtamov.neural_cutting.dto.vacancy.HhRuVacancyPreview;
import ru.shtamov.neural_cutting.dto.vacancy.ImportFromHhRuRequest;
import ru.shtamov.neural_cutting.dto.vacancy.SearchVacancyRequest;
import ru.shtamov.neural_cutting.dto.vacancy.VacancyResponse;
import ru.shtamov.neural_cutting.security.AuthenticatedUser;
import ru.shtamov.neural_cutting.service.VacancyService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vacancies")
@Tag(name = "💼 Вакансии", description = "Управление вакансиями и интеграция с HH.ru")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @PostMapping("/manual")
    @Operation(summary = "Создать вакансию вручную",
               description = "Создаёт новую вакансию с указанием названия, компании и описания")
    public ResponseEntity<VacancyResponse> createManual(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateManualVacancyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.createManual(user.id(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить вакансию",
               description = "Возвращает информацию о вакансии по ID")
    public VacancyResponse getById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Parameter(description = "ID вакансии") @PathVariable UUID id
    ) {
        return vacancyService.get(user.id(), id);
    }

    @GetMapping
    @Operation(summary = "Список вакансий",
               description = "Возвращает постраничный список вакансий текущего пользователя")
    public PageResponse<VacancyResponse> getAll(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return vacancyService.getAll(user.id(), pageable);
    }

    @PostMapping("/import/hh-ru")
    @Operation(summary = "Импорт вакансии из HH.ru",
               description = "Импортирует вакансию с HH.ru по ID или полной ссылке. " +
                            "Примеры: 'https://hh.ru/vacancy/92345678' или просто '92345678'")
    public ResponseEntity<VacancyResponse> importFromHhRu(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody ImportFromHhRuRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vacancyService.importFromHhRu(user.id(), request));
    }

    @PostMapping("/search/hh-ru")
    @Operation(summary = "Поиск вакансий на HH.ru",
               description = "Ищет вакансии на HH.ru без импорта. " +
                            "areaId: 1 — Москва, 2 — Санкт-Петербург, 113 — вся Россия")
    public List<HhRuVacancyPreview> searchFromHhRu(
            @Valid @RequestBody SearchVacancyRequest request
    ) {
        return vacancyService.searchFromHhRu(request);
    }
}
