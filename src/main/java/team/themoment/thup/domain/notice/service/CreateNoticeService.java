package team.themoment.thup.domain.notice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.notice.discord.NoticeDiscordNotifier;
import team.themoment.thup.domain.notice.discord.NoticeNotification;
import team.themoment.thup.domain.notice.dto.NoticeDetailResponse;
import team.themoment.thup.domain.notice.entity.NoticeJpaEntity;
import team.themoment.thup.domain.notice.repository.NoticeRepository;
import team.themoment.thup.domain.user.entity.UserJpaEntity;
import team.themoment.thup.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateNoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final NoticeDiscordNotifier noticeDiscordNotifier;

    public NoticeDetailResponse execute(OAuth2User admin, String title, String content) {
        NoticeContentValidator.validate(title, content);

        Long adminId = ((Number) admin.getAttribute("id")).longValue();
        UserJpaEntity createdBy = userRepository.findById(adminId)
                .orElseThrow(() -> new ExpectedException("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        NoticeJpaEntity saved = noticeRepository.save(
                NoticeJpaEntity.builder()
                        .createdBy(createdBy)
                        .title(title)
                        .content(content)
                        .build()
        );

        noticeDiscordNotifier.notifyCreated(new NoticeNotification(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                createdBy.getName()
        ));

        return NoticeDetailResponse.from(saved);
    }
}
