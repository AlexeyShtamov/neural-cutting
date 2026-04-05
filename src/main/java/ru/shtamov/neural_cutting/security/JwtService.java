package ru.shtamov.neural_cutting.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties properties;
    private SecretKey signingKey;

    public JwtService(JwtProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(properties.secret());
        } catch (RuntimeException exception) {
            keyBytes = properties.secret().getBytes(StandardCharsets.UTF_8);
        }
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(properties.expiration());
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(Map.of("uid", user.id().toString(), "name", user.name()))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaims(token).get("uid", String.class));
    }

    public boolean isTokenValid(String token, AuthenticatedUser user) {
        Claims claims = extractClaims(token);
        return user.getUsername().equalsIgnoreCase(claims.getSubject())
                && claims.getExpiration().after(new Date());
    }

    public long getExpirationSeconds() {
        return properties.expiration().toSeconds();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
