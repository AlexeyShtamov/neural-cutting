package ru.shtamov.neural_cutting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.Problem;

import java.util.UUID;

public interface ProblemRepository extends JpaRepository<Problem, UUID> {
}
