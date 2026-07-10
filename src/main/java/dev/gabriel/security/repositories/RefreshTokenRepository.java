package dev.gabriel.security.repositories;

import dev.gabriel.security.entities.RefreshToken;
import dev.gabriel.security.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(UUID token);

    @Modifying
    @Transactional
    void deleteAllByUser(User user);
}
