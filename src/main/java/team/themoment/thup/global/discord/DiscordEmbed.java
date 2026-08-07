package team.themoment.thup.global.discord;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 디스코드 웹훅 embed 페이로드.
 * 길이 제한(description 4096자, field value 1024자)을 넘기면 디스코드가 400을 반환하므로 절삭해서 담는다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DiscordEmbed(
        String title,
        String description,
        String url,
        Integer color,
        List<Field> fields,
        Footer footer,
        String timestamp
) {

    private static final int MAX_FIELD_VALUE_LENGTH = 1024;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Field(String name, String value, boolean inline) {

        public static Field of(String name, String value) {
            return new Field(name, truncate(value, MAX_FIELD_VALUE_LENGTH), false);
        }
    }

    public record Footer(String text) {
    }

    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
