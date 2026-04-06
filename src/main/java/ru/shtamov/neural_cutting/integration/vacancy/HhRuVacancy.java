package ru.shtamov.neural_cutting.integration.vacancy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * DTO representing a vacancy from HH.ru API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record HhRuVacancy(
        String id,
        String name,
        HhRuEmployer employer,
        HhRuSalary salary,
        HhRuArea area,
        HhRuSchedule schedule,
        HhRuExperience experience,
        HhRuEmployment employment,
        String description,
        @JsonProperty("key_skills") List<HhRuSkill> keySkills,
        @JsonProperty("published_at") Instant publishedAt,
        @JsonProperty("alternate_url") String alternateUrl,
        boolean archived,
        boolean premium
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuEmployer(
            String id,
            String name,
            @JsonProperty("alternate_url") String alternateUrl,
            @JsonProperty("logo_urls") Map<String, String> logoUrls
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuSalary(
            Long from,
            Long to,
            String currency,
            boolean gross
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuArea(
            String id,
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuSchedule(
            String id,
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuExperience(
            String id,
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuEmployment(
            String id,
            String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record HhRuSkill(
            String name
    ) {}
}
