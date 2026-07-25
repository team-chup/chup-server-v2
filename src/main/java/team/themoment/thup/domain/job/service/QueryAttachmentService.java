package team.themoment.thup.domain.job.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.themoment.sdk.exception.ExpectedException;
import team.themoment.thup.domain.job.entity.AttachmentJpaEntity;
import team.themoment.thup.domain.job.repository.AttachmentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryAttachmentService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentJpaEntity execute(Long jobId, Long fileId) {
        return attachmentRepository.findByIdAndJobPosting_Id(fileId, jobId)
                .orElseThrow(() -> new ExpectedException("첨부파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));
    }
}