package ru.shtamov.neural_cutting.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.AnalysisJob;

import java.util.Optional;
import java.util.UUID;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, UUID> {

    @EntityGraph(attributePaths = {"resumeVersion", "vacancy", "analysisResult"})
    Optional<AnalysisJob> findDetailedByIdAndResumeVersionResumeOwnerId(UUID id, UUID ownerId);

    @EntityGraph(attributePaths = {"analysisResult", "analysisResult.problems", "analysisResult.recommendations", "vacancy", "resumeVersion"})
    Optional<AnalysisJob> findWithResultByIdAndResumeVersionResumeOwnerId(UUID id, UUID ownerId);

    @EntityGraph(attributePaths = {"analysisResult", "vacancy"})
    Page<AnalysisJob> findAllByResumeVersionIdAndResumeVersionResumeOwnerId(UUID resumeVersionId, UUID ownerId, Pageable pageable);
}
