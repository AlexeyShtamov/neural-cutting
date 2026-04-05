package ru.shtamov.neural_cutting.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import ru.shtamov.neural_cutting.domain.Person;
import ru.shtamov.neural_cutting.dto.auth.AuthResponse;
import ru.shtamov.neural_cutting.dto.auth.LoginRequest;
import ru.shtamov.neural_cutting.dto.auth.RegisterRequest;
import ru.shtamov.neural_cutting.dto.auth.UserProfileResponse;
import ru.shtamov.neural_cutting.exception.ConflictException;
import ru.shtamov.neural_cutting.exception.NotFoundException;
import ru.shtamov.neural_cutting.mapper.AuthMapper;
import ru.shtamov.neural_cutting.repository.PersonRepository;
import ru.shtamov.neural_cutting.security.AuthenticatedUser;
import ru.shtamov.neural_cutting.security.JwtService;

import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    public AuthService(
            PersonRepository personRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            AuthMapper authMapper
    ) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authMapper = authMapper;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        if (personRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ConflictException("User with this email already exists");
        }

        Person person = new Person();
        person.setName(request.name().trim());
        person.setEmail(normalizedEmail);
        person.setPasswordHash(passwordEncoder.encode(request.password()));

        Person savedPerson = personRepository.save(person);
        log.info("Registered new user with email={}", normalizedEmail);
        return buildAuthResponse(savedPerson);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );

        Person person = personRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("User logged in with email={}", normalizedEmail);
        return buildAuthResponse(person);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMe(UUID userId) {
        Person person = personRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return authMapper.toUserProfile(person);
    }

    private AuthResponse buildAuthResponse(Person person) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getPasswordHash()
        );
        return new AuthResponse(
                jwtService.generateToken(authenticatedUser),
                "Bearer",
                jwtService.getExpirationSeconds(),
                authMapper.toUserProfile(person)
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
