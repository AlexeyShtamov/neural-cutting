package ru.shtamov.neural_cutting.mapper;

import org.springframework.stereotype.Component;
import ru.shtamov.neural_cutting.domain.Person;
import ru.shtamov.neural_cutting.dto.auth.UserProfileResponse;

@Component
public class AuthMapper {

    public UserProfileResponse toUserProfile(Person person) {
        return new UserProfileResponse(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getCreatedAt()
        );
    }
}
