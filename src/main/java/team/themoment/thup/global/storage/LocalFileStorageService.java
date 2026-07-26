package team.themoment.thup.global.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import team.themoment.sdk.exception.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;

    @Override
    public StoredFile store(MultipartFile file, String directory) {
        String originalFileName = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFileName);
        String storedFileName = UUID.randomUUID() + (extension == null ? "" : "." + extension);
        String storageKey = directory + "/" + storedFileName;

        try {
            Path target = resolve(storageKey);
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException e) {
            throw new ExpectedException("파일 저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new StoredFile(storageKey, originalFileName, file.getSize());
    }

    @Override
    public Resource loadAsResource(String storageKey) {
        Path path = resolve(storageKey);
        Resource resource = new FileSystemResource(path);
        if (!resource.exists() || !resource.isReadable()) {
            throw new ExpectedException("파일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
        return resource;
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException ignored) {
            // 파일 삭제 실패는 치명적이지 않으므로 무시
        }
    }

    private Path resolve(String storageKey) {
        Path baseDir = Path.of(fileStorageProperties.baseDir()).toAbsolutePath().normalize();
        Path target = baseDir.resolve(storageKey).normalize();
        if (!target.startsWith(baseDir)) {
            throw new ExpectedException("잘못된 파일 경로입니다.", HttpStatus.BAD_REQUEST);
        }
        return target;
    }
}