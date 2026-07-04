package dev.gabriel.security.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import dev.gabriel.security.entities.Role;
import dev.gabriel.security.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);

            var roles = user.getRoles()
                    .stream()
                    .map(Role::getName)
                    .toList();

            return JWT.create()
                    .withIssuer("gabriel")
                    .withSubject(user.getId().toString())
                    .withClaim("roles", roles)
                    .withExpiresAt(Instant.now().plusSeconds(15 * 60))
                    .sign(algorithm);

        } catch (JWTCreationException ex) {
            throw new RuntimeException("Error while generating token", ex);
        }
    }

    public String validateToken(String token) {
        try {
            var algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                    .withIssuer("gabriel")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException exception) {
            return "";
        }
    }
}
