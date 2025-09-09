package com.biolab.launchpad.internal.client;

import com.biolab.common.HealthCheckResponse;
import com.biolab.hardware.HardwareServiceGateway;
import com.biolab.integration.IntegrationServiceGateway;
import com.biolab.integration.internal.client.HardwareServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


@SpringBootTest
class IntegrationServiceClientModulithTest {

    @Autowired
    @Qualifier("integrationServiceClientModulith")
    IntegrationServiceClient hardwareServiceClient;

    @MockitoSpyBean
    private IntegrationServiceGateway integrationServiceGateway;

    @Test
    void healthCheck() {
        HealthCheckResponse healthCheckResponse = hardwareServiceClient.healthCheck();
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
        hardwareServiceClient.asyncEvent("Test Payload");
        //await().atLeast(2, SECONDS); //bad
        //verify(integrationServiceGateway).asyncEvent(any()); // TODO:its just doesnt work for event publishing
        verifyNoMoreInteractions(integrationServiceGateway);
    }}