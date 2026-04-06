package ru.shtamov.neural_cutting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.Skill;
import ru.shtamov.neural_cutting.domain.enums.SkillCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findByNormalizedName(String normalizedName);

    List<Skill> findByCategory(SkillCategory category);

    boolean existsByNormalizedName(String normalizedName);
}
