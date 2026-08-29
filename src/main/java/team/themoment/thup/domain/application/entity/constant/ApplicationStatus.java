package team.themoment.thup.domain.application.entity.constant;

public enum ApplicationStatus {
    APPLIED,             // 지원 접수 / 결과 대기
    DOCUMENT_FAILED,     // 서류 탈락
    INTERVIEW_SCHEDULED, // 면접 예정
    PASSED,              // 최종 합격
    FAILED               // 면접 탈락
}
