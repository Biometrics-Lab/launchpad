package com.biolab.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataSourceType {
    JSON_CONFIG("JSON_CONFIG"),
    SCRIPT("SCRIPT"),
    MAPPING("MAPPING");

    private final String value;
}
