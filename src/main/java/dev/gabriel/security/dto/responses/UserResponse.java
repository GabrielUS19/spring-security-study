package dev.gabriel.security.dto.responses;

import java.util.UUID;

public record UserResponse (
        UUID   id,
        String name,
        String email
) {
}
