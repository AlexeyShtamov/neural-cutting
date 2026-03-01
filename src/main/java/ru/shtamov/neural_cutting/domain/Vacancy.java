package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;

    private String company;
    private String url; // url место, где хранится вакансия
    private String text;

    @OneToMany(mappedBy = "vacancy")
    private List<ResumeVersion> resumeVersionList = new ArrayList<>();
}
