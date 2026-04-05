package ru.shtamov.neural_cutting.integration.analysis;

import ru.shtamov.neural_cutting.domain.enums.SourceType;

import java.util.UUID;

public record ExternalAnalysisRequest(
        ResumePayload resume,
        VacancyPayload vacancy
) {
    public record ResumePayload(
            UUID resumeVersionId,
            String resumeTitle,
            String text,
            SourceType sourceType,
            String fileName,
            String filePath
    ) {
    }

    public record VacancyPayload(
            UUID vacancyId,
            String title,
            String company,
            String text
    ) {
    }
}
