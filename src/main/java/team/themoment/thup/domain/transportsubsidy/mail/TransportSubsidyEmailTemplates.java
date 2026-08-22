package team.themoment.thup.domain.transportsubsidy.mail;

import team.themoment.thup.domain.transportsubsidy.entity.constant.TransportSubsidyStatus;

public final class TransportSubsidyEmailTemplates {

    private TransportSubsidyEmailTemplates() {
    }

    public static String resultSubject(String companyName, TransportSubsidyStatus status) {
        return "[CHUP] %s 면접 교통비 지원 %s 안내".formatted(companyName, resultLabel(status));
    }

    public static String resultBody(String companyName, TransportSubsidyStatus status) {
        return """
                <p>%s 면접 교통비 지원 신청이 <strong>%s</strong>되었습니다.</p>
                <p>자세한 내용은 CHUP에서 확인하실 수 있습니다.</p>
                """.formatted(companyName, resultLabel(status));
    }

    private static String resultLabel(TransportSubsidyStatus status) {
        return switch (status) {
            case PENDING -> "대기";
            case APPROVED -> "승인";
            case REJECTED -> "거절";
        };
    }
}
