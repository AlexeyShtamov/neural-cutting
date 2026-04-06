package ru.shtamov.neural_cutting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shtamov.neural_cutting.domain.Person;
import ru.shtamov.neural_cutting.domain.Vacancy;
import ru.shtamov.neural_cutting.dto.common.PageResponse;
import ru.shtamov.neural_cutting.dto.vacancy.CreateManualVacancyRequest;
import ru.shtamov.neural_cutting.dto.vacancy.HhRuVacancyPreview;
import ru.shtamov.neural_cutting.dto.vacancy.ImportFromHhRuRequest;
import ru.shtamov.neural_cutting.dto.vacancy.SearchVacancyRequest;
import ru.shtamov.neural_cutting.dto.vacancy.VacancyResponse;
import ru.shtamov.neural_cutting.exception.BadRequestException;
import ru.shtamov.neural_cutting.exception.NotFoundException;
import ru.shtamov.neural_cutting.integration.vacancy.HhRuVacancy;
import ru.shtamov.neural_cutting.integration.vacancy.HhRuSearchResponse;
import ru.shtamov.neural_cutting.integration.vacancy.VacancyProvider;
import ru.shtamov.neural_cutting.mapper.VacancyMapper;
import ru.shtamov.neural_cutting.repository.PersonRepository;
import ru.shtamov.neural_cutting.repository.VacancyRepository;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.UUID;

@Service
@Slf4j
public class VacancyService {

    private static final Pattern HH_RU_URL_PATTERN = Pattern.compile(
            "https?://(?:www\\.)?hh\\.ru/vacancy/(\\d+)", Pattern.CASE_INSENSITIVE
    );

    private final VacancyRepository vacancyRepository;
    private final PersonRepository personRepository;
    private final VacancyMapper vacancyMapper;
    private final Optional<VacancyProvider> vacancyProvider;

    public VacancyService(
            VacancyRepository vacancyRepository,
            PersonRepository personRepository,
            VacancyMapper vacancyMapper,
            Optional<VacancyProvider> vacancyProvider
    ) {
        this.vacancyRepository = vacancyRepository;
        this.personRepository = personRepository;
        this.vacancyMapper = vacancyMapper;
        this.vacancyProvider = vacancyProvider;
    }

    @Transactional
    public VacancyResponse createManual(UUID ownerId, CreateManualVacancyRequest request) {
        Person owner = personRepository.getReferenceById(ownerId);
        Vacancy vacancy = new Vacancy();
        vacancy.setOwner(owner);
        vacancy.setTitle(request.title().trim());
        vacancy.setCompany(request.company() == null ? null : request.company().trim());
        vacancy.setUrl(request.url() == null ? null : request.url().trim());
        vacancy.setText(request.text().trim());
        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        log.info("Created vacancy id={} for owner={}", savedVacancy.getId(), ownerId);
        return vacancyMapper.toResponse(savedVacancy);
    }

    @Transactional(readOnly = true)
    public VacancyResponse get(UUID ownerId, UUID vacancyId) {
        return vacancyMapper.toResponse(getOwnedVacancy(ownerId, vacancyId));
    }

    @Transactional(readOnly = true)
    public PageResponse<VacancyResponse> getAll(UUID ownerId, Pageable pageable) {
        Page<VacancyResponse> page = vacancyRepository.findAllByOwnerId(ownerId, pageable)
                .map(vacancyMapper::toResponse);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public Vacancy getOwnedVacancy(UUID ownerId, UUID vacancyId) {
        return vacancyRepository.findByIdAndOwnerId(vacancyId, ownerId)
                .orElseThrow(() -> new NotFoundException("Vacancy not found"));
    }

    /**
     * Import a vacancy from HH.ru by ID or URL.
     *
     * @param ownerId the owner UUID
     * @param request the import request containing vacancy ID or URL
     * @return the created vacancy response
     * @throws BadRequestException if HH.ru integration is not available or vacancy not found
     */
    @Transactional
    public VacancyResponse importFromHhRu(UUID ownerId, ImportFromHhRuRequest request) {
        VacancyProvider provider = vacancyProvider
                .orElseThrow(() -> new BadRequestException("HH.ru integration is not available"));

        String vacancyId = extractVacancyId(request.vacancyIdOrUrl());

        HhRuVacancy hhVacancy = provider.findById(vacancyId)
                .orElseThrow(() -> new BadRequestException("Vacancy not found on HH.ru: " + vacancyId));

        Person owner = personRepository.getReferenceById(ownerId);
        Vacancy vacancy = convertFromHhRu(hhVacancy, owner);

        Vacancy savedVacancy = vacancyRepository.save(vacancy);
        log.info("Imported vacancy id={} from HH.ru vacancyId={} for owner={}",
                savedVacancy.getId(), vacancyId, ownerId);

        return vacancyMapper.toResponse(savedVacancy);
    }

    /**
     * Search for vacancies on HH.ru.
     *
     * @param request the search request
     * @return list of matching vacancy previews
     * @throws BadRequestException if HH.ru integration is not available
     */
    public List<HhRuVacancyPreview> searchFromHhRu(SearchVacancyRequest request) {
        VacancyProvider provider = vacancyProvider
                .orElseThrow(() -> new BadRequestException("HH.ru integration is not available"));

        HhRuSearchResponse response = provider.search(
                request.query(),
                request.areaId(),
                request.limit()
        );

        return response.items().stream()
                .map(this::convertToPreview)
                .toList();
    }

    private String extractVacancyId(String vacancyIdOrUrl) {
        Matcher matcher = HH_RU_URL_PATTERN.matcher(vacancyIdOrUrl.trim());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        // Assume it's already an ID
        return vacancyIdOrUrl.trim();
    }

    private Vacancy convertFromHhRu(HhRuVacancy hhVacancy, Person owner) {
        Vacancy vacancy = new Vacancy();
        vacancy.setOwner(owner);
        vacancy.setTitle(hhVacancy.name());
        vacancy.setCompany(hhVacancy.employer() != null ? hhVacancy.employer().name() : null);
        vacancy.setUrl(hhVacancy.alternateUrl());
        vacancy.setText(buildVacancyText(hhVacancy));
        return vacancy;
    }

    private String buildVacancyText(HhRuVacancy hhVacancy) {
        StringBuilder sb = new StringBuilder();

        if (hhVacancy.description() != null) {
            sb.append(hhVacancy.description());
        }

        if (hhVacancy.keySkills() != null && !hhVacancy.keySkills().isEmpty()) {
            sb.append("\n\nКлючевые навыки:\n");
            hhVacancy.keySkills().forEach(skill -> sb.append("- ").append(skill.name()).append("\n"));
        }

        return sb.toString();
    }

    private HhRuVacancyPreview convertToPreview(HhRuSearchResponse.HhRuVacancyPreview preview) {
        HhRuVacancyPreview.SalaryInfo salaryInfo = null;
        if (preview.salary() != null) {
            salaryInfo = new HhRuVacancyPreview.SalaryInfo(
                    preview.salary().from(),
                    preview.salary().to(),
                    preview.salary().currency(),
                    preview.salary().gross()
            );
        }

        return new HhRuVacancyPreview(
                preview.id(),
                preview.name(),
                preview.employer() != null ? preview.employer().name() : null,
                preview.employer() != null && preview.employer().logoUrls() != null
                        ? preview.employer().logoUrls().get("90")
                        : null,
                salaryInfo,
                preview.area() != null ? preview.area().name() : null,
                preview.alternateUrl(),
                preview.publishedAt() != null ? java.time.Instant.parse(preview.publishedAt()) : null
        );
    }
}
