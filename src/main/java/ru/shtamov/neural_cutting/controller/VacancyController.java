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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shtamov.neural_cutting.dto.common.PageResponse;
import ru.shtamov.neural_cutting.dto.vacancy.CreateManualVacancyRequest;
import ru.shtamov.neural_cutting.dto.vacancy.VacancyResponse;
import ru.shtamov.neural_cutting.security.AuthenticatedUser;
import ru.shtamov.neural_cutting.service.VacancyService;

import java.util.UUID;

@RestController
@RequestMapping("/api/vacancies")
@Tag(name = "Vacancies", description = "Vacancy management")
public class VacancyController {

    private final VacancyService vacancyService;

    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @PostMapping("/manual")
    @Operation(summary = "Create vacancy manually")
    public ResponseEntity<VacancyResponse> createManual(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @RequestBody CreateManualVacancyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacancyService.createManual(user.id(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vacancy by id")
    public VacancyResponse getById(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PathVariable UUID id
    ) {
        return vacancyService.get(user.id(), id);
    }

    @GetMapping
    @Operation(summary = "Get paginated list of vacancies")
    public PageResponse<VacancyResponse> getAll(
            @AuthenticationPrincipal AuthenticatedUser user,
            @ParameterObject @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return vacancyService.getAll(user.id(), pageable);
    }
}
