package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import ru.shtamov.neural_cutting.domain.enums.SourceType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ResumeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Integer versionN;
    private String url;
    private String text;
    private SourceType sourceType;
    private LocalDate createdAt;

    @ManyToOne
    private Resume resume;

    @ManyToOne
    private Vacancy vacancy;

    @OneToMany(mappedBy = "resumeVersion", cascade = CascadeType.ALL)
    private List<AnalysisJob> analysisJobList = new ArrayList<>();
}
