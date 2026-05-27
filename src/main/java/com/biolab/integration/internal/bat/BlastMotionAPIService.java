package com.biolab.integration.internal.bat;

import com.biolab.common.*;
import com.biolab.integration.internal.service.ResourceStorageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j2
public class BlastMotionAPIService implements BadSensorService {

    private final ApplicationEventPublisher eventPublisher;
    private final ResourceStorageService resourceStorageService;
    private final ObjectMapper objectMapper;
    private final TransactionTemplate transactionTemplate;
    private final long repIntervalMs;
    private final double resourceDelayProbability;
    private final ScheduledExecutorService scheduler;
    private final List<Map<BatSensorMetric, Double>> mockReps;

    private final Map<Integer, ScheduledFuture<?>> activeSessions = new ConcurrentHashMap<>();
    private final Map<Integer, AtomicInteger> repCounters = new ConcurrentHashMap<>();

    public BlastMotionAPIService(
            ApplicationEventPublisher eventPublisher,
            ResourceStorageService resourceStorageService,
            ObjectMapper objectMapper,
            TransactionTemplate transactionTemplate,
            @Value("${app.blast-motion.rep-interval-ms:2000}") long repIntervalMs,
            @Value("${app.blast-motion.resource-delay-probability:0.3}") double resourceDelayProbability,
            @Value("${app.blast-motion.scheduler-threads:10}") int schedulerThreads) {
        this.eventPublisher = eventPublisher;
        this.resourceStorageService = resourceStorageService;
        this.objectMapper = objectMapper;
        this.transactionTemplate = transactionTemplate;
        this.repIntervalMs = repIntervalMs;
        this.resourceDelayProbability = resourceDelayProbability;
        this.scheduler = Executors.newScheduledThreadPool(schedulerThreads);
        this.mockReps = loadMockReps();
    }

    @Override
    public void startSession(Integer sessionId, List<SessionMetricConfig> metrics) {
        List<SessionMetricConfig> blastMetrics = metrics.stream()
                .filter(m -> isBlastMotion(m.dataSource().content()))
                .toList();

        if (blastMetrics.isEmpty()) {
            log.debug("No Blast Motion metrics for session={}, skipping", sessionId);
            return;
        }

        repCounters.put(sessionId, new AtomicInteger(0));
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> generateRep(sessionId, blastMetrics),
                repIntervalMs, repIntervalMs, TimeUnit.MILLISECONDS);
        activeSessions.put(sessionId, future);
        log.info("BlastMotionAPIService started for session={} metrics={}", sessionId, blastMetrics.size());
    }

    @Override
    public void stopSession(Integer sessionId) {
        ScheduledFuture<?> future = activeSessions.remove(sessionId);
        if (future != null) {
            future.cancel(false);
            repCounters.remove(sessionId);
            log.info("BlastMotionAPIService stopped for session={}", sessionId);
        }
    }

    private void generateRep(Integer sessionId, List<SessionMetricConfig> metrics) {
        try {
            AtomicInteger counter = repCounters.get(sessionId);
            if (counter == null) return;
            int repNumber = counter.incrementAndGet();
            Map<BatSensorMetric, Double> row = mockReps.get((repNumber - 1) % mockReps.size());

            List<RepMetricData> metricData = metrics.stream()
                    .map(m -> buildMetricData(m, row))
                    .filter(Objects::nonNull)
                    .toList();

            UUID uuid = UUID.randomUUID();
            String internalUrl = resourceStorageService.getInternalUrl(uuid, "swing.mp4");
            boolean delayed = Math.random() < resourceDelayProbability;

            List<RepResourceData> resources = List.of(
                    new RepResourceData(internalUrl, UrlStatus.PENDING, uuid));

            RepDataReceivedEvent repEvent = new RepDataReceivedEvent(
                    sessionId, repNumber, Timestamp.from(Instant.now()), metricData, resources);
            transactionTemplate.executeWithoutResult(tx -> eventPublisher.publishEvent(repEvent));

            byte[] placeholder = new byte[]{0};
            if (delayed) {
                scheduler.schedule(
                        () -> resourceStorageService.upload(placeholder, "video/mp4", "swing.mp4", uuid),
                        3000, TimeUnit.MILLISECONDS);
            } else {
                resourceStorageService.upload(placeholder, "video/mp4", "swing.mp4", uuid);
            }

        } catch (Exception e) {
            log.error("Error generating rep for session={}", sessionId, e);
        }
    }

    private RepMetricData buildMetricData(SessionMetricConfig config, Map<BatSensorMetric, Double> row) {
        try {
            JsonNode node = objectMapper.readTree(config.dataSource().content());
            BatSensorMetric metric = BatSensorMetric.valueOf(node.path("metric").asText());
            double value = row.getOrDefault(metric, 0.0);
            return new RepMetricData(config.conditionalMetricId(), config.dataSource().id(), value);
        } catch (Exception e) {
            log.warn("Could not parse metric for conditionalMetricId={}", config.conditionalMetricId());
            return null;
        }
    }

    private boolean isBlastMotion(String content) {
        try {
            JsonNode node = objectMapper.readTree(content);
            return Integration.BLAST_MOTION_API_DEMO.id.equals(node.path("integrationId").asText());
        } catch (Exception e) {
            return false;
        }
    }

    private List<Map<BatSensorMetric, Double>> loadMockReps() {
        try (var stream = getClass().getClassLoader()
                    .getResourceAsStream("mock/blast-motion-reps.csv");
             var reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)))) {

            String[] headers = reader.readLine().split(",");
            BatSensorMetric[] metricHeaders = Arrays.stream(headers)
                    .map(h -> BatSensorMetric.valueOf(h.trim()))
                    .toArray(BatSensorMetric[]::new);

            List<Map<BatSensorMetric, Double>> rows = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] values = line.split(",");
                Map<BatSensorMetric, Double> row = new LinkedHashMap<>();
                for (int i = 0; i < metricHeaders.length; i++) {
                    row.put(metricHeaders[i], Double.parseDouble(values[i].trim()));
                }
                rows.add(row);
            }
            return Collections.unmodifiableList(rows);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load mock rep data from mock/blast-motion-reps.csv", e);
        }
    }
}
