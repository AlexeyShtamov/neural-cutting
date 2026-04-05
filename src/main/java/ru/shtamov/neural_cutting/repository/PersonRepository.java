package ru.shtamov.neural_cutting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shtamov.neural_cutting.domain.Person;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    Optional<Person> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
