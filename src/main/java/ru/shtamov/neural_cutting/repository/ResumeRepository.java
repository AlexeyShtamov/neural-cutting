package ru.shtamov.neural_cutting.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.Resume;

import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    Page<Resume> findAllByOwnerId(UUID ownerId, Pageable pageable);

    Optional<Resume> findByIdAndOwnerId(UUID id, UUID ownerId);

    @EntityGraph(attributePaths = "versions")
    Optional<Resume> findWithVersionsByIdAndOwnerId(UUID id, UUID ownerId);
}
