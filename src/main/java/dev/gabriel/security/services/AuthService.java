package dev.gabriel.security.services;

import dev.gabriel.security.dto.requests.LoginRequest;
import dev.gabriel.security.dto.responses.LoginResponse;
import dev.gabriel.security.entities.RefreshToken;
import dev.gabriel.security.infra.security.CustomUserDetails;
import dev.gabriel.security.infra.security.TokenService;
import dev.gabriel.security.repositories.RefreshTokenRepository;
import dev.gabriel.security.repositories.RoleRepository;
import dev.gabriel.security.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;
    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, PasswordEncoder encoder, TokenService tokenService, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public LoginResult login(LoginRequest request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = authenticationManager.authenticate(usernamePassword);
        var user = (CustomUserDetails) auth.getPrincipal();

        var accessToken = tokenService.generateToken(user);
        var refreshToken = refreshTokenService.generateRefreshToken(user);

        return new LoginResult(user.name(), accessToken, refreshToken.toString());
    }

    public record LoginResult(
            String name,
            String accessToken,
            String refreshToken
    ) {}
}
