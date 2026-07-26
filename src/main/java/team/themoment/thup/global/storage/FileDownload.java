package team.themoment.thup.global.storage;

import org.springframework.core.io.Resource;

public record FileDownload(Resource resource, String fileName) {
}