package com.biolab.hardware;

import com.biolab.common.AsyncRequest;
import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest
@DisplayName("HardwareServiceGateway Tests")
class HardwareServiceGatewayTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @MockitoSpyBean
    HardwareServiceGateway hardwareServiceGateway;

    @Test
    void healthCheck() {
        HealthCheckResponse healthCheckResponse = hardwareServiceGateway.healthCheck(
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

    @Test
    void asyncEvent() {
        AsyncRequest asyncRequest = AsyncRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .payload("Test Payload")
                .build();

        eventPublisher.publishEvent(asyncRequest);

        //await().atLeast(2, SECONDS); //bad
        //verify(hardwareServiceGateway).asyncEvent(any());
        verifyNoMoreInteractions(hardwareServiceGateway);

    }
}