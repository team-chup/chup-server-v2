package team.themoment.thup.domain.auth.dto;

public record AuthorizationUrlResult(String url, String state, String redirectOrigin) {
}