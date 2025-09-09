package com.biolab.integration;

import com.biolab.common.AsyncRequest;
import com.biolab.common.HealthCheckRequest;
import com.biolab.common.HealthCheckResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("IntegrationServiceGateway Tests")
class IntegrationServiceGatewayTest {

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @MockitoSpyBean
    private IntegrationServiceGateway integrationServiceGateway;

    @Test
    void healthCheck() {
        HealthCheckResponse healthCheckResponse = integrationServiceGateway.healthCheck(
                HealthCheckRequest.builder()
                        .requestId(UUID.randomUUID().toString())
                        .build()
        );

        var expectedResponse = HealthCheckResponse.builder()
                .requestId(healthCheckResponse.requestId())
                .status("OK")
                .build();

        assertEquals(expectedResponse, healthCheckResponse);
        verify(integrationServiceGateway).healthCheck(any());
        verifyNoMoreInteractions(integrationServiceGateway);

    }

    @Test
    void asyncEvent() {
        eventPublisher.publishEvent("Test Payload");

        await().atLeast(2, SECONDS); //bad
        //verify(integrationServiceGateway).asyncEvent(any());
        verifyNoMoreInteractions(integrationServiceGateway);
    }
}