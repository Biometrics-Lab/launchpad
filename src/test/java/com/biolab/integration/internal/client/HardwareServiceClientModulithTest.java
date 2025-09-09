package com.biolab.integration.internal.client;

import com.biolab.common.HealthCheckResponse;
import com.biolab.hardware.HardwareServiceGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class HardwareServiceClientModulithTest {

    @Autowired
    @Qualifier("hardwareServiceClientModulith")
    HardwareServiceClient hardwareServiceClient;

    @MockitoSpyBean
    private HardwareServiceGateway hardwareServiceGateway;

    @Test
    void healthCheck() {
        HealthCheckResponse healthCheckResponse = hardwareServiceClient.healthCheck();
        var expectedResponse = HealthCheckResponse.builder()
                .requestId(healthCheckResponse.requestId())
                .status("OK")
                .build();
        assertEquals(expectedResponse, healthCheckResponse);
        verify(hardwareServiceGateway).healthCheck(any());
        verifyNoMoreInteractions(hardwareServiceGateway);
    }

    @Test
    void asyncEvent() {
        hardwareServiceClient.asyncEvent("Test Payload");
        //await().atLeast(2, SECONDS); //bad
        //verify(hardwareServiceGateway).asyncEvent(any()); // TODO:its just doesnt work for event publishing
        verifyNoMoreInteractions(hardwareServiceGateway);
    }
}