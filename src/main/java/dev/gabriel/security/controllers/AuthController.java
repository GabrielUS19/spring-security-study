package dev.gabriel.security.controllers;

import dev.gabriel.security.dto.requests.LoginRequest;
import dev.gabriel.security.dto.requests.RegisterRequest;
import dev.gabriel.security.dto.responses.LoginResponse;
import dev.gabriel.security.dto.responses.RegisterResponse;
import dev.gabriel.security.entities.User;
import dev.gabriel.security.exceptions.EmailAlreadyRegisteredException;
import dev.gabriel.security.exceptions.ResourceNotFoundException;
import dev.gabriel.security.infra.security.CustomUserDetails;
import dev.gabriel.security.infra.security.TokenService;
import dev.gabriel.security.repositories.RoleRepository;
import dev.gabriel.security.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        var auth = authenticationManager.authenticate(usernamePassword);

        var user = (CustomUserDetails) auth.getPrincipal();

        var token = tokenService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(user.name(), token));
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        if (!userRepository.findByEmail(request.email()).isEmpty()) {
            throw new EmailAlreadyRegisteredException("This email is already in use");
        }

        var role = roleRepository.findByName("ROLE_ADMI")
                .orElseThrow(() -> new IllegalArgumentException("Invalid Role: ROLE_ADMIN"));

        var user = new User();
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(role));

        var savedUser = userRepository.save(user);

        return ResponseEntity.ok(new RegisterResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail()));
    }

    @GetMapping("/test")
    public ResponseEntity testError() {
        throw new ResourceNotFoundException("Não encontrado");
    }
}
