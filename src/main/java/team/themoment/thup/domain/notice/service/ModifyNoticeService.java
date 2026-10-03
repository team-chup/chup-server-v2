package team.themoment.thup.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.notice.dto.NoticeDetailResponse;
import team.themoment.thup.domain.notice.entity.NoticeJpaEntity;
import team.themoment.thup.domain.notice.repository.NoticeRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class ModifyNoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeDetailResponse execute(Long noticeId, String title, String content) {
        NoticeContentValidator.validate(title, content);

        NoticeJpaEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ExpectedException("공지사항을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        notice.update(title, content);

        // updatedAt(@UpdateTimestamp)은 flush 시점에 갱신되므로 응답에 반영되도록 먼저 flush한다
        noticeRepository.flush();

        return NoticeDetailResponse.from(notice);
    }
}
