package team.themoment.thup.domain.user.dto;

import team.themoment.thup.domain.user.entity.ResumeJpaEntity;

import java.time.LocalDateTime;

public record ResumeResponse(
        Long id,
        String fileName,
        Integer fileSize,
        LocalDateTime createdAt
) {
    public static ResumeResponse from(ResumeJpaEntity resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileSize(),
                resume.getCreatedAt()
        );
    }
}
