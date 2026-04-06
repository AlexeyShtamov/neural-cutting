package ru.shtamov.neural_cutting.dto.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Превью вакансии из HH.ru")
public record HhRuVacancyPreview(
        @Schema(description = "ID вакансии на HH.ru", example = "92345678")
        String id,

        @Schema(description = "Название вакансии", example = "Java Backend Developer")
        String name,

        @Schema(description = "Название компании", example = "TechCorp")
        String employerName,

        @Schema(description = "URL логотипа компании")
        String employerLogoUrl,

        @Schema(description = "Информация о зарплате")
        SalaryInfo salary,

        @Schema(description = "Название региона", example = "Москва")
        String areaName,

        @Schema(description = "Ссылка на вакансию", example = "https://hh.ru/vacancy/92345678")
        String alternateUrl,

        @Schema(description = "Дата публикации")
        Instant publishedAt
) {

    @Schema(description = "Информация о зарплате")
    public record SalaryInfo(
            @Schema(description = "Минимальная зарплата", example = "150000")
            Long from,

            @Schema(description = "Максимальная зарплата", example = "250000")
            Long to,

            @Schema(description = "Валюта", example = "RUR")
            String currency,

            @Schema(description = "До вычета налогов", example = "false")
            Boolean gross
    ) {

        public String getFormatted() {
            if (from == null && to == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            if (from != null) {
                sb.append("от ").append(formatNumber(from));
            }
            if (to != null) {
                if (from != null) {
                    sb.append(" ");
                }
                sb.append("до ").append(formatNumber(to));
            }
            if (currency != null) {
                sb.append(" ").append(currency);
            }
            return sb.toString();
        }

        private String formatNumber(Long value) {
            return String.format("%,d", value).replace('\u00A0', ' ');
        }
    }
}
