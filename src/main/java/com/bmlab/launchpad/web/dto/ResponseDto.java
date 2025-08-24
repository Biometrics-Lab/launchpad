package com.bmlab.launchpad.web.dto;

/**
 * Represents an message response with a status code and an message message.
 * This is used to standardize message responses across the application.
 */

import lombok.Builder;

@Builder
public record ResponseDto(
        Integer status,
        String message
) {}

