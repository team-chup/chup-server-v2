package team.themoment.thup.global.storage;

import org.springframework.http.HttpStatus;
import team.themoment.sdk.exception.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipBuilder {

    private ZipBuilder() {
    }

    public record Entry(String path, String storageKey) {}

    public static byte[] build(List<Entry> entries, FileStorageService fileStorageService) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(buffer)) {
            for (Entry entry : entries) {
                zipOutputStream.putNextEntry(new ZipEntry(entry.path()));
                try (InputStream inputStream = fileStorageService.loadAsResource(entry.storageKey()).getInputStream()) {
                    inputStream.transferTo(zipOutputStream);
                }
                zipOutputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new ExpectedException("ZIP 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return buffer.toByteArray();
    }

    public static String uniqueName(String fileName, Map<String, Integer> usedNames) {
        int count = usedNames.merge(fileName, 1, Integer::sum);
        if (count == 1) {
            return fileName;
        }
        int dotIndex = fileName.lastIndexOf('.');
        String base = dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
        String extension = dotIndex == -1 ? "" : fileName.substring(dotIndex);
        return "%s(%d)%s".formatted(base, count - 1, extension);
    }
}
