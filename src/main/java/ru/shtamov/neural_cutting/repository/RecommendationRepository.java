package ru.shtamov.neural_cutting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.Recommendation;

import java.util.UUID;

public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {
}
