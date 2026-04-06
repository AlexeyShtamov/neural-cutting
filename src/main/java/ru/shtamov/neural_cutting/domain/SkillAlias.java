package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "skill_aliases", indexes = {
        @Index(name = "idx_skill_aliases_normalized_alias", columnList = "normalized_alias")
})
public class SkillAlias extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String alias;

    @Column(nullable = false, length = 200, unique = true)
    private String normalizedAlias;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Skill skill;
}
