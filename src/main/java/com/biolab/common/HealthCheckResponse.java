package com.biolab.common;

import lombok.Builder;

@Builder
public record HealthCheckResponse(
        String requestId,
        String status
) {}