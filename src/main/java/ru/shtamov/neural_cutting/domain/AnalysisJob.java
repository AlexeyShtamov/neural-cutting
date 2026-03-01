package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;
import ru.shtamov.neural_cutting.domain.enums.AnalysisJobStatus;

import java.time.LocalDate;

@Entity
@Data
public class AnalysisJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private AnalysisJobStatus status;

    private Integer errorCode;
    private String errorMessage;

    private LocalDate startedAt;
    private LocalDate finishedAt;

    @ManyToOne
    private ResumeVersion resumeVersion;

    @OneToOne(mappedBy = "analysisJob")
    private AnalysisResult analysisResult;
}
