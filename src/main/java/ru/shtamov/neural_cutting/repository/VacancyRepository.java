package ru.shtamov.neural_cutting.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.Vacancy;

import java.util.Optional;
import java.util.UUID;

public interface VacancyRepository extends JpaRepository<Vacancy, UUID> {

    Page<Vacancy> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Optional<Vacancy> findByIdAndOwnerId(UUID id, UUID ownerId);
}
