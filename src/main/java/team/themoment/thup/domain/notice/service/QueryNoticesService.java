package team.themoment.thup.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.thup.domain.notice.dto.NoticeSummaryResponse;
import team.themoment.thup.domain.notice.repository.NoticeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryNoticesService {

    private final NoticeRepository noticeRepository;

    public List<NoticeSummaryResponse> execute() {
        return noticeRepository.findAllByOrderByCreatedAtDescIdDesc().stream()
                .map(NoticeSummaryResponse::from)
                .toList();
    }
}
