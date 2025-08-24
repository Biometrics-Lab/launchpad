package com.bmlab.launchpad.security.exceptions;

public class NotFoundByException extends RuntimeException {
    public NotFoundByException(String message, Object ... args) {
        super(message.formatted(args));
    }
}
