package ru.shtamov.neural_cutting.dto.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание вакансии вручную")
public record CreateManualVacancyRequest(
        @Schema(description = "Название вакансии", example = "Java Backend Developer")
        @NotBlank @Size(max = 200) String title,

        @Schema(description = "Название компании", example = "TechCorp")
        @Size(max = 200) String company,

        @Schema(description = "Ссылка на вакансию", example = "https://hh.ru/vacancy/12345678")
        @Size(max = 500) String url,

        @Schema(description = "Текст описания вакансии",
                example = """
                Требования:
                • Опыт разработки на Java от 3 лет
                • Знание Spring Boot, Spring Security
                • Опыт работы с PostgreSQL, MongoDB
                • Понимание микросервисной архитектуры

                Обязанности:
                • Разработка и поддержка микросервисов
                • Написание unit и integration тестов
                • Code review, работа в команде

                Условия:
                • Официальное трудоустройство
                • ДМС, фитнес
                • Удалённая работа
                """)
        @NotBlank @Size(max = 50000) String text
) {
}
