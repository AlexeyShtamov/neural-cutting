package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.shtamov.neural_cutting.domain.enums.AnalysisJobStatus;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "analysis_jobs")
public class AnalysisJob extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AnalysisJobStatus status;

    private Integer errorCode;

    @Column(columnDefinition = "text")
    private String errorMessage;

    private Instant startedAt;

    private Instant finishedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ResumeVersion resumeVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Vacancy vacancy;

    @OneToOne(mappedBy = "analysisJob")
    private AnalysisResult analysisResult;
}
