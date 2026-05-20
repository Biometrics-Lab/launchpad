package com.biolab.common;

public enum ResourceContentType {

    VIDEO("Video"),
    IMAGE("Image"),
    CSV("CSV");

    private final String value;

    ResourceContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
