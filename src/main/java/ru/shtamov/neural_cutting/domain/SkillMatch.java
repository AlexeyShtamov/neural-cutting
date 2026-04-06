package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "skill_matches")
public class SkillMatch extends BaseEntity {

    @Column(nullable = false)
    private Integer matchPercent;

    @Column(nullable = false)
    private Integer matchedSkillsCount;

    @Column(nullable = false)
    private Integer missingSkillsCount;

    @Column(columnDefinition = "text")
    private String matchedSkills;

    @Column(columnDefinition = "text")
    private String missingSkills;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_result_id", unique = true)
    private AnalysisResult analysisResult;
}
