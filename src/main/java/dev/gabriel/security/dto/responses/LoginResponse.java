package dev.gabriel.security.dto.responses;

public record LoginResponse (
        String name,
        String accessToken
) {
}
