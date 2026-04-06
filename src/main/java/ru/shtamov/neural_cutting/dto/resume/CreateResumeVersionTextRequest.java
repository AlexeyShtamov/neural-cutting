package ru.shtamov.neural_cutting.dto.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание текстовой версии резюме")
public record CreateResumeVersionTextRequest(
        @Schema(description = "Текст резюме",
                example = """
                Иванов Иван Иванович
                Java Backend Developer

                Опыт работы:
                • 3 года в компании TechCorp — разработка микросервисов на Spring Boot
                • Опыт работы с PostgreSQL, MongoDB, Redis
                • Развёртывание в Kubernetes, CI/CD на GitLab

                Навыки:
                • Java 17+, Spring Boot, Spring Security, Hibernate
                • PostgreSQL, MongoDB, Redis
                • Docker, Kubernetes, Git
                • Командная разработка, Agile/Scrum

                Образование:
                • Бакалавриат, Информационные технологии, МГУ (2018-2022)
                """)
        @NotBlank @Size(max = 50000) String text
) {
}
