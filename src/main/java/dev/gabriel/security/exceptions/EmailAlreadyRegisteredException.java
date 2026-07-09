package dev.gabriel.security.exceptions;

import org.springframework.http.HttpStatus;

public class EmailAlreadyRegisteredException extends BaseException {
    public EmailAlreadyRegisteredException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
