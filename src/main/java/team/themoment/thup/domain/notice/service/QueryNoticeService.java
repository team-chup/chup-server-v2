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
@Transactional(readOnly = true)
public class QueryNoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeDetailResponse execute(Long noticeId) {
        NoticeJpaEntity notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new ExpectedException("공지사항을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
        return NoticeDetailResponse.from(notice);
    }
}
