package com.biolab.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("Admin"),
    COACH("Coach"),
    PLAYER("Player");

    private final String value;
}
