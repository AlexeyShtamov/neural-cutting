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
import lombok.Getter;
import lombok.Setter;
import ru.shtamov.neural_cutting.domain.enums.Language;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "resumes")
public class Resume extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Language language;

    @Column(nullable = false, length = 200)
    private String targetRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person owner;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("versionNumber DESC")
    private List<ResumeVersion> versions = new ArrayList<>();
}
