package team.themoment.thup.domain.job.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.entity.constant.AttachmentFileType;

public final class AttachmentFileTypeResolver {

    private AttachmentFileTypeResolver() {
    }

    public static AttachmentFileType resolve(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();

        if (fileName.endsWith(".pdf") || "application/pdf".equals(contentType)) {
            return AttachmentFileType.PDF;
        }
        if (fileName.endsWith(".hwpx")) {
            return AttachmentFileType.HWPX;
        }
        if (fileName.endsWith(".hwp")) {
            return AttachmentFileType.HWP;
        }
        if (contentType != null && contentType.startsWith("image/")) {
            return AttachmentFileType.IMAGE;
        }

        throw new ExpectedException("지원하지 않는 첨부파일 형식입니다. (PDF/HWP/HWPX/이미지만 가능)", HttpStatus.BAD_REQUEST);
    }
}