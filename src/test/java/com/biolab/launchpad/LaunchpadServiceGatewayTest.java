package com.biolab.launchpad;

import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@DisplayName("LaunchpadServiceGateway Tests")
class LaunchpadServiceGatewayTest {
    @Autowired
    LaunchpadServiceGateway launchpadServiceGateway;

    @Test
    void healthCheck() {
        HealthCheckResponse healthCheckResponse = launchpadServiceGateway.healthCheck(
                HealthCheckRequest.builder()
                        .requestId(UUID.randomUUID().toString())
                        .build()
        );

        var expectedResponse = HealthCheckResponse.builder()
                .requestId(healthCheckResponse.requestId())
                .status("OK")
                .build();

        assertEquals(expectedResponse, healthCheckResponse);
    }
}