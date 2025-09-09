package com.biolab.common;


import lombok.Builder;

@Builder
public record AsyncRequest(
        String consumerServiceName,
        Object payload,
        String requestId
) {}
