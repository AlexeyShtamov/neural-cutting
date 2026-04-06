package ru.shtamov.neural_cutting.integration.vacancy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO representing a search response from HH.ru API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record HhRuSearchResponse(
        List<HhRuVacancyPreview> items,
        @JsonProperty("found") int totalFound,
        @JsonProperty("per_page") int perPage,
        int page,
        int pages
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuVacancyPreview(
            String id,
            String name,
            HhRuVacancy.HhRuEmployer employer,
            HhRuVacancy.HhRuSalary salary,
            HhRuVacancy.HhRuArea area,
            @JsonProperty("alternate_url") String alternateUrl,
            @JsonProperty("published_at") String publishedAt
    ) {}
}
