package dev.gabriel.security.services;

import dev.gabriel.security.entities.RefreshToken;
import dev.gabriel.security.infra.security.CustomUserDetails;
import dev.gabriel.security.repositories.RefreshTokenRepository;
import dev.gabriel.security.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    UUID generateRefreshToken(CustomUserDetails userDetails) {
        var user = userRepository.getReferenceById(userDetails.id());

        var refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID());
        refreshToken.setRevoked(false);
        refreshToken.setExpiresAt(Instant.now().plusSeconds(1296000));

        var savedToken = refreshTokenRepository.save(refreshToken);
        return savedToken.getToken();
    }
}
