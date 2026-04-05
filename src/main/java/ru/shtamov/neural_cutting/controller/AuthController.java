package ru.shtamov.neural_cutting.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.shtamov.neural_cutting.dto.auth.AuthResponse;
import ru.shtamov.neural_cutting.dto.auth.LoginRequest;
import ru.shtamov.neural_cutting.dto.auth.RegisterRequest;
import ru.shtamov.neural_cutting.dto.auth.UserProfileResponse;
import ru.shtamov.neural_cutting.security.AuthenticatedUser;
import ru.shtamov.neural_cutting.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "User registration and JWT authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and obtain bearer token")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user")
    public UserProfileResponse me(@AuthenticationPrincipal AuthenticatedUser user) {
        return authService.getMe(user.id());
    }
}
