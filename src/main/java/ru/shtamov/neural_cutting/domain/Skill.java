package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.shtamov.neural_cutting.domain.enums.SkillCategory;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "skills")
public class Skill extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SkillCategory category;

    @Column(nullable = false, length = 200, unique = true)
    private String normalizedName;

    @OneToMany(mappedBy = "skill")
    private List<SkillAlias> aliases = new ArrayList<>();
}
