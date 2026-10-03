package team.themoment.thup.domain.notice.service;

import org.springframework.http.HttpStatus;
import team.themoment.sdk.exception.ExpectedException;

final class NoticeContentValidator {

    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CONTENT_LENGTH = 10000;

    private NoticeContentValidator() {
    }

    static void validate(String title, String content) {
        if (title == null || title.isBlank()) {
            throw new ExpectedException("제목을 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new ExpectedException("제목은 " + MAX_TITLE_LENGTH + "자 이하로 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (content == null || content.isBlank()) {
            throw new ExpectedException("내용을 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new ExpectedException("내용은 " + MAX_CONTENT_LENGTH + "자 이하로 입력해야 합니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
