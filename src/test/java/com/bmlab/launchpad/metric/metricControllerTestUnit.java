package com.bmlab.launchpad.metric;

import com.bmlab.launchpad.controller.MetricController;
import com.bmlab.launchpad.dto.MetricDTO;
import com.bmlab.launchpad.repository.Metric;
import com.bmlab.launchpad.service.MetricService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class metricControllerTestUnit {
    @InjectMocks
    private MetricController metricController;
    @Mock
    private MetricService metricService;

    @Test
    void createTestVerificationName() {

        MetricDTO metricDTO = Mockito.mock(MetricDTO.class);
        Mockito.when(metricDTO.getName()).thenReturn(null);

        ResponseEntity<?> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name must not be null");
        ResponseEntity<?> actual   = metricController.create(metricDTO);

        assertEquals(expected, actual);
    }

    @Test
    void createTestFailed() {

        MetricDTO metricDTO = Mockito.mock(MetricDTO.class);
        Mockito.when(metricService.create(metricDTO)).thenReturn(null);
        Mockito.when(metricDTO.getName()).thenReturn("test");

        ResponseEntity<?> expected = ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Metric could not be created with provided data");
        ResponseEntity<?> actual   = metricController.create(metricDTO);

        assertEquals(expected, actual);
    }

    @Test
    void createTestCreateEntity() {

        Metric metric = Metric.builder()
                .name("Test metric")
                .build();

        MetricDTO metricDTO = Mockito.mock(MetricDTO.class);
        when(metricService.create(metricDTO)).thenReturn(metric);
        when(metricDTO.getName()).thenReturn("test");

        HttpStatusCode expected = ResponseEntity.ok(metric).getStatusCode();
        HttpStatusCode actual   = metricController.create(metricDTO).getStatusCode();

        assertEquals(expected, actual);
    }
}
