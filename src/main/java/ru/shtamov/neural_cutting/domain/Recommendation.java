package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.shtamov.neural_cutting.domain.enums.RecommendationPriority;

@Getter
@Setter
@Entity
@Table(name = "recommendations")
public class Recommendation extends BaseEntity {

    @Column(nullable = false, columnDefinition = "text")
    private String action;

    @Column(columnDefinition = "text")
    private String example;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecommendationPriority priority;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AnalysisResult analysisResult;
}
