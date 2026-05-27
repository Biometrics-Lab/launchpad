package com.biolab.launchpad;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.RepDataReceivedEvent;
import com.biolab.common.RepMetricData;
import com.biolab.common.RepResourceData;
import com.biolab.common.UrlStatus;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.RepResource;
import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.service.SessionNotificationService;
import com.biolab.launchpad.internal.web.controller.EntityFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@DisplayName("RepDataReceivedEvent Listener Tests")
class RepDataReceivedEventListenerTest {

    @Autowired ApplicationEventPublisher eventPublisher;
    @Autowired TransactionTemplate transactionTemplate;
    @Autowired RepRepository repRepository;
    @Autowired RepMetricRepository repMetricRepository;
    @Autowired RepResourceRepository repResourceRepository;
    @Autowired EntityFactory entityFactory;
    @MockitoSpyBean SessionNotificationService sessionNotificationService;

    Session session;

    @BeforeEach
    void setUp() {
        entityFactory.createResourceTypeDictionary("Video");
        session = entityFactory.createActiveSession();
    }

    @AfterEach
    void tearDown() {
        repResourceRepository.deleteAll();
        repMetricRepository.deleteAll();
        repRepository.deleteAll();
        entityFactory.cleanup();
    }

    @Test
    @DisplayName("RepDataReceivedEvent -> Rep and RepMetric rows persisted")
    void repAndMetricsPersisted() {
        var conditionalMetric = entityFactory.createConditionalMetric();
        entityFactory.createAssessmentMetric(session.getAssessmentId(), conditionalMetric.getId());

        var event = new RepDataReceivedEvent(
                session.getId(),
                1,
                Timestamp.valueOf(LocalDateTime.now()),
                List.of(new RepMetricData(conditionalMetric.getId(), null, 42.5)),
                List.of()
        );

        transactionTemplate.executeWithoutResult(tx -> eventPublisher.publishEvent(event));

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                !repRepository.findAllBySessionId(session.getId()).isEmpty());

        var reps = repRepository.findAllBySessionId(session.getId());
        assertEquals(1, reps.size());

        var repMetrics = repMetricRepository.findAllByRepIdIn(List.of(reps.get(0).getId()));
        assertEquals(1, repMetrics.size());
        assertEquals(conditionalMetric.getId(), repMetrics.get(0).getConditionalMetricId());
        assertEquals(42.5, repMetrics.get(0).getValue().doubleValue(), 0.001);

        verify(sessionNotificationService).broadcastRep(eq(session.getId()), any());
    }

    @Test
    @DisplayName("RepDataReceivedEvent with resources -> RepResource rows persisted")
    void repResourcePersisted() {
        var url1 = "http://localhost:4566/biolab-resources/resources/test/cam1.mp4";
        var url2 = "http://localhost:4566/biolab-resources/resources/test/cam2.mp4";
        var event = new RepDataReceivedEvent(
                session.getId(),
                1,
                Timestamp.valueOf(LocalDateTime.now()),
                List.of(),
                List.of(
                        new RepResourceData(url1, UrlStatus.READY, UUID.randomUUID()),
                        new RepResourceData(url2, UrlStatus.PENDING, UUID.randomUUID())
                )
        );

        transactionTemplate.executeWithoutResult(tx -> eventPublisher.publishEvent(event));

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                !repRepository.findAllBySessionId(session.getId()).isEmpty());

        var reps = repRepository.findAllBySessionId(session.getId());
        assertEquals(1, reps.size());

        var resources = (List<RepResource>) repResourceRepository.findAll();
        assertEquals(2, resources.size());

        var ready = resources.stream().filter(r -> r.getUrl().equals(url1)).findFirst().orElseThrow();
        assertEquals(UrlStatus.READY, ready.getUrlStatus());
        assertEquals(reps.get(0).getId(), ready.getRepId());

        var pending = resources.stream().filter(r -> r.getUrl().equals(url2)).findFirst().orElseThrow();
        assertEquals(UrlStatus.PENDING, pending.getUrlStatus());
    }

    @Test
    @DisplayName("RepDataReceivedEvent with unknown conditionalMetricId -> metric silently dropped")
    void invalidMetricDropped() {
        var event = new RepDataReceivedEvent(
                session.getId(),
                1,
                Timestamp.valueOf(LocalDateTime.now()),
                List.of(new RepMetricData(999999, null, 99.0)),
                List.of()
        );

        transactionTemplate.executeWithoutResult(tx -> eventPublisher.publishEvent(event));

        await().atMost(10, TimeUnit.SECONDS).until(() ->
                !repRepository.findAllBySessionId(session.getId()).isEmpty());

        var reps = repRepository.findAllBySessionId(session.getId());
        var repMetrics = repMetricRepository.findAllByRepIdIn(List.of(reps.get(0).getId()));
        assertTrue(repMetrics.isEmpty());
    }
}
