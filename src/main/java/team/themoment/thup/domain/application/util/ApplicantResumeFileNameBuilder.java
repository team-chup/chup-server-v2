package team.themoment.thup.domain.application.util;

import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;

public final class ApplicantResumeFileNameBuilder {

    private ApplicantResumeFileNameBuilder() {
    }

    // 외부 지원 건(jobPosition 없음)은 이력서가 존재하지 않아 호출부에서 이 메서드에 도달하지 않는다는 전제에 의존한다.
    public static String buildLabel(ApplicationJpaEntity application) {
        String name = sanitize(application.getUser().getName());
        String studentId = sanitize(application.getUser().getStudentId());
        String position = sanitize(application.getJobPosition().getName());

        return "%s_%s_%s".formatted(name, studentId, position);
    }

    public static String buildZipFileName(ApplicationJpaEntity application) {
        return buildLabel(application) + ".zip";
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[\\\\/:*?\"<>|]", "");
    }
}
