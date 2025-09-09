package com.biolab.common;


import lombok.Builder;

@Builder
public record HealthCheckRequest(
        String requestId
) {}
