package ru.shtamov.neural_cutting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.SkillAlias;

import java.util.Optional;
import java.util.UUID;

public interface SkillAliasRepository extends JpaRepository<SkillAlias, UUID> {

    Optional<SkillAlias> findByNormalizedAlias(String normalizedAlias);

    boolean existsByNormalizedAlias(String normalizedAlias);
}
