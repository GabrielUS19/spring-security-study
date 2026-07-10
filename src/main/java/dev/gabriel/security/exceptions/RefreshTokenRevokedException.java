package dev.gabriel.security.exceptions;

import org.springframework.http.HttpStatus;

public class RefreshTokenRevokedException extends BaseException {
    public RefreshTokenRevokedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
