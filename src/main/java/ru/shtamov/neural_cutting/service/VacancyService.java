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
import ru.shtamov.neural_cutting.dto.vacancy.VacancyResponse;
import ru.shtamov.neural_cutting.exception.NotFoundException;
import ru.shtamov.neural_cutting.mapper.VacancyMapper;
import ru.shtamov.neural_cutting.repository.PersonRepository;
import ru.shtamov.neural_cutting.repository.VacancyRepository;

import java.util.UUID;

@Service
@Slf4j
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final PersonRepository personRepository;
    private final VacancyMapper vacancyMapper;

    public VacancyService(VacancyRepository vacancyRepository, PersonRepository personRepository, VacancyMapper vacancyMapper) {
        this.vacancyRepository = vacancyRepository;
        this.personRepository = personRepository;
        this.vacancyMapper = vacancyMapper;
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
}
