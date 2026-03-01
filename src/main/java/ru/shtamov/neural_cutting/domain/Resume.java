package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import ru.shtamov.neural_cutting.domain.enums.Language;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;

    @Enumerated(EnumType.STRING)
    private Language language;

    private String tarterRole;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ManyToOne
    private Person owner;

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumeVersion> versions;
}
