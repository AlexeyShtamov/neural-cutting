package ru.shtamov.neural_cutting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "vacancies")
public class Vacancy extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String company;

    @Column(length = 500)
    private String url;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Person owner;

    @OneToMany(mappedBy = "vacancy")
    @OrderBy("createdAt DESC")
    private List<AnalysisJob> analysisJobs = new ArrayList<>();
}
