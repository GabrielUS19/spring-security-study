package dev.gabriel.security.dto.responses;

import java.util.UUID;

public record RegisterResponse(
        UUID id,
        String name,
        String email
) {
}
