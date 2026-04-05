package ru.shtamov.neural_cutting.mapper;

import org.springframework.stereotype.Component;
import ru.shtamov.neural_cutting.domain.Vacancy;
import ru.shtamov.neural_cutting.dto.vacancy.VacancyResponse;

@Component
public class VacancyMapper {

    public VacancyResponse toResponse(Vacancy vacancy) {
        return new VacancyResponse(
                vacancy.getId(),
                vacancy.getTitle(),
                vacancy.getCompany(),
                vacancy.getUrl(),
                vacancy.getText(),
                vacancy.getCreatedAt(),
                vacancy.getUpdatedAt()
        );
    }
}
