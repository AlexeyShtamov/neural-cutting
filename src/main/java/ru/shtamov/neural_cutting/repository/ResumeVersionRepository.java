package ru.shtamov.neural_cutting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shtamov.neural_cutting.domain.ResumeVersion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeVersionRepository extends JpaRepository<ResumeVersion, UUID> {

    List<ResumeVersion> findAllByResumeIdAndResumeOwnerIdOrderByVersionNumberDesc(UUID resumeId, UUID ownerId);

    Optional<ResumeVersion> findByIdAndResumeOwnerId(UUID id, UUID ownerId);

    @EntityGraph(attributePaths = {"resume", "resume.owner"})
    Optional<ResumeVersion> findDetailedByIdAndResumeOwnerId(UUID id, UUID ownerId);

    @Query("select coalesce(max(rv.versionNumber), 0) from ResumeVersion rv where rv.resume.id = :resumeId")
    int findMaxVersionNumberByResumeId(@Param("resumeId") UUID resumeId);
}
