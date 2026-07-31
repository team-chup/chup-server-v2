package team.themoment.thup.global.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    StoredFile store(MultipartFile file, String directory);

    Resource loadAsResource(String storageKey);

    void delete(String storageKey);
}