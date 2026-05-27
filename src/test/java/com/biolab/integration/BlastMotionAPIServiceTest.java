package com.biolab.integration;

import com.biolab.common.*;
import com.biolab.integration.internal.bat.BatSensorMetric;
import com.biolab.integration.internal.bat.BlastMotionAPIService;
import com.biolab.integration.internal.service.ResourceStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("BlastMotionAPIService")
class BlastMotionAPIServiceTest {

    ResourceStorageService resourceStorageService;
    BlastMotionAPIService service;
    List<RepDataReceivedEvent> capturedEvents;

    @BeforeEach
    void setUp() {
        capturedEvents = new CopyOnWriteArrayList<>();
        resourceStorageService = mock(ResourceStorageService.class);
        when(resourceStorageService.getInternalUrl(any(UUID.class), anyString()))
                .thenAnswer(inv -> "http://localhost:4566/bucket/resources/" + inv.getArgument(0) + "/swing.mp4");
        when(resourceStorageService.upload(any(), anyString(), anyString(), any(UUID.class)))
                .thenReturn("http://localhost:4566/bucket/resources/uuid/swing.mp4");

        TransactionTemplate txTemplate = mock(TransactionTemplate.class);
        doAnswer(inv -> {
            Consumer<TransactionStatus> action = inv.getArgument(0);
            action.accept(null);
            return null;
        }).when(txTemplate).executeWithoutResult(any());

        service = new BlastMotionAPIService(
                event -> { if (event instanceof RepDataReceivedEvent e) capturedEvents.add(e); },
                resourceStorageService,
                new ObjectMapper(),
                txTemplate,
                200L,   // fast interval for tests
                0.0,    // no delay — all resources uploaded immediately
                2);
    }

    @AfterEach
    void tearDown() {
        service.stopSession(1);
        service.stopSession(2);
    }

    private SessionMetricConfig blastConfig(BatSensorMetric metric) {
        String content = "{\"integrationId\":\"blast-motion-api-demo\",\"metric\":\"" + metric.name() + "\"}";
        return new SessionMetricConfig(42, new DataSourceConfig(1, "BatSpeed DS", "BAT_SENSOR", content));
    }

    @Test
    @DisplayName("generates reps with incrementing repNumbers")
    void generatesRepsWithIncrementingRepNumbers() {
        service.startSession(1, List.of(blastConfig(BatSensorMetric.BAT_SPEED)));

        await().atMost(3, TimeUnit.SECONDS).until(() -> capturedEvents.size() >= 3);
        service.stopSession(1);

        assertEquals(1, capturedEvents.get(0).repNumber());
        assertEquals(2, capturedEvents.get(1).repNumber());
        assertEquals(3, capturedEvents.get(2).repNumber());
    }

    @Test
    @DisplayName("cycles through CSV rows — rep 6 equals rep 1")
    void cyclesThroughCsvRows() {
        service.startSession(1, List.of(blastConfig(BatSensorMetric.BAT_SPEED)));

        await().atMost(5, TimeUnit.SECONDS).until(() -> capturedEvents.size() >= 6);
        service.stopSession(1);

        double rep1Value = capturedEvents.get(0).metrics().get(0).value().doubleValue();
        double rep6Value = capturedEvents.get(5).metrics().get(0).value().doubleValue();
        assertEquals(rep1Value, rep6Value, 0.001);
    }

    @Test
    @DisplayName("two sessions have independent counters starting at 1")
    void twoSessionsHaveIndependentCounters() {
        var metrics = List.of(blastConfig(BatSensorMetric.BAT_SPEED));
        service.startSession(1, metrics);

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                capturedEvents.stream().filter(e -> e.sessionId().equals(1)).count() >= 2);

        service.startSession(2, metrics);

        await().atMost(2, TimeUnit.SECONDS).until(() ->
                capturedEvents.stream().filter(e -> e.sessionId().equals(2)).count() >= 1);

        service.stopSession(1);
        service.stopSession(2);

        var session2Events = capturedEvents.stream().filter(e -> e.sessionId().equals(2)).toList();
        assertEquals(1, session2Events.get(0).repNumber());
    }

    @Test
    @DisplayName("ignores sessions with no Blast Motion metrics")
    void ignoresNonBlastMotionMetrics() {
        var nonBlastConfig = new SessionMetricConfig(99,
                new DataSourceConfig(2, "Other DS", "OTHER",
                        "{\"integrationId\":\"other-integration\"}"));
        service.startSession(1, List.of(nonBlastConfig));

        try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        service.stopSession(1);
        assertTrue(capturedEvents.isEmpty());
    }

    @Test
    @DisplayName("metric value matches CSV for known metric")
    void metricValueMatchesCsv() {
        service.startSession(1, List.of(blastConfig(BatSensorMetric.BAT_SPEED)));

        await().atMost(2, TimeUnit.SECONDS).until(() -> !capturedEvents.isEmpty());
        service.stopSession(1);

        double batSpeed = capturedEvents.get(0).metrics().get(0).value().doubleValue();
        assertEquals(68.5, batSpeed, 0.001); // first CSV row value
    }
}
