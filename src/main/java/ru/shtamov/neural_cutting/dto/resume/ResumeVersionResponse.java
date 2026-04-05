package ru.shtamov.neural_cutting.dto.resume;

import ru.shtamov.neural_cutting.domain.enums.SourceType;

import java.time.Instant;
import java.util.UUID;

public record ResumeVersionResponse(
        UUID id,
        UUID resumeId,
        Integer versionNumber,
        SourceType sourceType,
        String textContent,
        String originalFileName,
        String contentType,
        Long fileSize,
        Instant createdAt,
        Instant updatedAt
) {
}
