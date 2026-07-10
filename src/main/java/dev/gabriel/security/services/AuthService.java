package dev.gabriel.security.services;

import dev.gabriel.security.dto.requests.LoginRequest;
import dev.gabriel.security.dto.requests.RegisterRequest;
import dev.gabriel.security.dto.responses.LoginResponse;
import dev.gabriel.security.dto.responses.RegisterResponse;
import dev.gabriel.security.entities.RefreshToken;
import dev.gabriel.security.entities.Role;
import dev.gabriel.security.entities.User;
import dev.gabriel.security.exceptions.EmailAlreadyRegisteredException;
import dev.gabriel.security.exceptions.ResourceNotFoundException;
import dev.gabriel.security.infra.security.CustomUserDetails;
import dev.gabriel.security.infra.security.TokenService;
import dev.gabriel.security.repositories.RefreshTokenRepository;
import dev.gabriel.security.repositories.RoleRepository;
import dev.gabriel.security.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        var roles = Set.of("ROLE_USER");

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException("Email already in use: " + request.email());
        }

        var foundRoles = roleRepository.findByNameIn(roles);

        if (foundRoles.size() != roles.size()) {
            var foundNames = foundRoles.stream().map(role -> role.getName()).collect(Collectors.toSet());
            var invalidRoles = roles.stream()
                    .filter(roleName -> !foundNames.contains(roleName))
                    .toList();

            throw new ResourceNotFoundException("Invalid Roles provided: " + invalidRoles);
        }

        var user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(encoder.encode(request.password()));
        user.setRoles(foundRoles);

        try {
            var savedUser = userRepository.save(user);
            return new RegisterResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());

        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyRegisteredException("Email already in use: " + request.email());
        }
    }

    public record LoginResult(
            String name,
            String accessToken,
            String refreshToken
    ) {}
}
