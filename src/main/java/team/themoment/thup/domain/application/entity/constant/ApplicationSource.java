package team.themoment.thup.domain.application.entity.constant;

public enum ApplicationSource {
    OFFICIAL, // 시스템 공식 공고 지원 (ApplyJobService에서만 사용)
    EXTERNAL  // 외부 플랫폼 지원 (수동 등록). 실제 플랫폼명은 sourcePlatform에 자유 입력
}
