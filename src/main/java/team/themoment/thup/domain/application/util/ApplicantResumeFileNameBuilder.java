package team.themoment.thup.domain.application.util;

import team.themoment.thup.domain.application.entity.ApplicationJpaEntity;

public final class ApplicantResumeFileNameBuilder {

    private ApplicantResumeFileNameBuilder() {
    }

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
