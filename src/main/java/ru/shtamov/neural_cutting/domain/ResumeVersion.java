package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import ru.shtamov.neural_cutting.domain.enums.SourceType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "resume_versions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_resume_version_number", columnNames = {"resume_id", "version_number"})
        }
)
public class ResumeVersion extends BaseEntity {

    @Column(nullable = false, name = "version_number")
    private Integer versionNumber;

    @Column(columnDefinition = "text")
    private String textContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private SourceType sourceType;

    @Column(length = 512)
    private String storagePath;

    @Column(length = 255)
    private String originalFileName;

    @Column(length = 100)
    private String contentType;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Resume resume;

    @OneToMany(mappedBy = "resumeVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<AnalysisJob> analysisJobs = new ArrayList<>();
}
