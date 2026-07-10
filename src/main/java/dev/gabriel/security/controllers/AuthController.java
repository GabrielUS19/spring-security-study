package dev.gabriel.security.controllers;

import dev.gabriel.security.dto.requests.LoginRequest;
import dev.gabriel.security.dto.requests.RegisterRequest;
import dev.gabriel.security.dto.responses.LoginResponse;
import dev.gabriel.security.dto.responses.RegisterResponse;
import dev.gabriel.security.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        var loginResult = authService.login(request);

        var refreshTokenCookie = ResponseCookie.from("refreshToken", loginResult.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(15))
                .sameSite("Strict")
                .build();

        var loginResponse = new LoginResponse(loginResult.name(), loginResult.accessToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        var response = authService.register(request);

        var location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@CookieValue(name = "refreshToken") String refreshToken) {
        var refreshTokenResult = authService.refreshToken(UUID.fromString(refreshToken));

        var refreshTokenCookie = ResponseCookie.from("refreshToken", refreshTokenResult.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(15))
                .sameSite("strict")
                .build();

        var loginResponse = new LoginResponse(refreshTokenResult.name(), refreshTokenResult.accessToken());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(loginResponse);
    }
}
