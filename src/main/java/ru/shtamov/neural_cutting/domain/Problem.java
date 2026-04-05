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
import ru.shtamov.neural_cutting.domain.enums.ProblemCategory;
import ru.shtamov.neural_cutting.domain.enums.ProblemSeverity;

@Getter
@Setter
@Entity
@Table(name = "problems")
public class Problem extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ProblemCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProblemSeverity severity;

    @Column(columnDefinition = "text")
    private String fragment;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AnalysisResult analysisResult;
}
