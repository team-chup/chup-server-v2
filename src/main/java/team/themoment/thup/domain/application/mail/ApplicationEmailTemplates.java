package team.themoment.thup.domain.application.mail;

import team.themoment.thup.domain.application.entity.constant.ApplicationStatus;

public final class ApplicationEmailTemplates {

    private ApplicationEmailTemplates() {
    }

    public static String appliedSubject(String companyName) {
        return "[CHUP] %s 지원이 완료되었습니다".formatted(companyName);
    }

    public static String appliedBody(String companyName, String positionName) {
        return """
                <p>%s(%s) 포지션 지원이 정상적으로 접수되었습니다.</p>
                <p>지원 결과는 CHUP에서 확인하실 수 있습니다.</p>
                """.formatted(companyName, positionName);
    }

    public static String resultSubject(String companyName, ApplicationStatus status) {
        return "[CHUP] %s 지원 결과 안내 (%s)".formatted(companyName, resultLabel(status));
    }

    public static String resultBody(String companyName, String positionName, ApplicationStatus status) {
        return """
                <p>%s(%s) 포지션 지원 결과, <strong>서류 %s</strong>하셨습니다.</p>
                <p>자세한 내용은 CHUP에서 확인하실 수 있습니다.</p>
                """.formatted(companyName, positionName, resultLabel(status));
    }

    private static String resultLabel(ApplicationStatus status) {
        return status == ApplicationStatus.PASSED ? "서류 합격" : "서류 불합격";
    }
}