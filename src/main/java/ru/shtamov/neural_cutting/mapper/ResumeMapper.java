package ru.shtamov.neural_cutting.mapper;

import org.springframework.stereotype.Component;
import ru.shtamov.neural_cutting.domain.Resume;
import ru.shtamov.neural_cutting.domain.ResumeVersion;
import ru.shtamov.neural_cutting.dto.resume.ResumeDetailResponse;
import ru.shtamov.neural_cutting.dto.resume.ResumeResponse;
import ru.shtamov.neural_cutting.dto.resume.ResumeVersionResponse;

import java.util.List;

@Component
public class ResumeMapper {

    public ResumeResponse toResponse(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getTitle(),
                resume.getLanguage(),
                resume.getTargetRole(),
                resume.getCreatedAt(),
                resume.getUpdatedAt()
        );
    }

    public ResumeDetailResponse toDetailResponse(Resume resume, List<ResumeVersionResponse> versions) {
        return new ResumeDetailResponse(
                resume.getId(),
                resume.getTitle(),
                resume.getLanguage(),
                resume.getTargetRole(),
                resume.getCreatedAt(),
                resume.getUpdatedAt(),
                versions
        );
    }

    public ResumeVersionResponse toVersionResponse(ResumeVersion version) {
        return new ResumeVersionResponse(
                version.getId(),
                version.getResume().getId(),
                version.getVersionNumber(),
                version.getSourceType(),
                version.getTextContent(),
                version.getOriginalFileName(),
                version.getContentType(),
                version.getFileSize(),
                version.getCreatedAt(),
                version.getUpdatedAt()
        );
    }
}
