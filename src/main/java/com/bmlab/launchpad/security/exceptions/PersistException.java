package com.bmlab.launchpad.security.exceptions;

public class PersistException extends RuntimeException {
    public PersistException(String message, Object ... args) {
        super(message.formatted(args));
    }
}
