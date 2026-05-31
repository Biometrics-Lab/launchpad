package com.biolab.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sport {
    BASEBALL("Baseball"),
    SOFTBALL("Softball");

    private final String value;
}
