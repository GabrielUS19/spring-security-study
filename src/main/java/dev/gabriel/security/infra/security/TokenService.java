package dev.gabriel.security.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import dev.gabriel.security.entities.Role;
import dev.gabriel.security.entities.User;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(UserDetails user) {
        try {
            var algorithm = Algorithm.HMAC256(secret);

            var roles = user.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            return JWT.create()
                    .withIssuer("gabriel")
                    .withSubject(user.getUsername())
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

        } catch (TokenExpiredException exception) {
            throw new BadCredentialsException("JWT Token Expired");

        } catch (JWTVerificationException exception) {
            throw new BadCredentialsException("Invalid JWT Token");
        }
    }
}
