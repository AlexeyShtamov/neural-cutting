package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import ru.shtamov.neural_cutting.domain.enums.RecommendationType;

import java.time.LocalDate;

@Entity
@Data
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private RecommendationType recommendationType;

    private String text;

    private LocalDate createdAt;

    @ManyToOne
    private Problem problem;
}
