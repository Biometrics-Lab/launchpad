package com.biolab.launchpad.internal.security.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message, Object... args) {
        super(message.formatted(args));
    }
}
