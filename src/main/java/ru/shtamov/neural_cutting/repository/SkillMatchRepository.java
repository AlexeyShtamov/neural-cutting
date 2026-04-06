package ru.shtamov.neural_cutting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.SkillMatch;

import java.util.Optional;
import java.util.UUID;

public interface SkillMatchRepository extends JpaRepository<SkillMatch, UUID> {

    Optional<SkillMatch> findByAnalysisResultId(UUID analysisResultId);

    boolean existsByAnalysisResultId(UUID analysisResultId);
}
