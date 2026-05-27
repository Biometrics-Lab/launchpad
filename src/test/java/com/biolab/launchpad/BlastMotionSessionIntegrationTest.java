package com.biolab.launchpad;

import com.biolab.TestcontainersConfiguration;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.web.controller.EntityFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@DisplayName("Blast Motion full pipeline: POST /start -> reps in DB")
class BlastMotionSessionIntegrationTest {

    private static final String API = "/api/v1/sessions";

    @Autowired MockMvc mvc;
    @Autowired EntityFactory entityFactory;
    @Autowired RepRepository repRepository;
    @Autowired RepMetricRepository repMetricRepository;
    @Autowired RepResourceRepository repResourceRepository;

    com.biolab.launchpad.internal.repository.model.Session session;

    @BeforeEach
    void setUp() {
        session = entityFactory.createBlastMotionSession();
    }

    @AfterEach
    void tearDown() throws Exception {
        if ("ACTIVE".equals(session.getStatus())) {
            mvc.perform(post(API + "/" + session.getId() + "/stop")
                    .contentType(MediaType.APPLICATION_JSON)
                    .with(httpBasic("biolab", "biolab")));
        }
        repResourceRepository.deleteAll();
        repMetricRepository.deleteAll();
        repRepository.deleteAll();
        entityFactory.cleanup();
    }

    @Test
    @DisplayName("reps with metrics and resources are persisted after session start")
    void repsWithMetricsAndResourcesPersistedAfterStart() throws Exception {
        mvc.perform(post(API + "/" + session.getId() + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk());

        session.setStatus("ACTIVE");

        await().atMost(15, TimeUnit.SECONDS)
                .until(() -> !repRepository.findAllBySessionId(session.getId()).isEmpty());

        List<Rep> reps = repRepository.findAllBySessionId(session.getId());
        assertFalse(reps.isEmpty());

        Rep first = reps.get(0);
        assertEquals(1, first.getRepNumber());

        var metrics = repMetricRepository.findAllByRepIdIn(List.of(first.getId()));
        assertEquals(1, metrics.size());
        assertEquals(68.5, metrics.get(0).getValue().doubleValue(), 0.001);

        var resources = repResourceRepository.findAllByRepId(first.getId());
        assertEquals(1, resources.size());
        assertNotNull(resources.get(0).getUuid());
        assertNotNull(resources.get(0).getUrl());
    }

    @Test
    @DisplayName("rep numbers increment across consecutive reps")
    void repNumbersIncrement() throws Exception {
        mvc.perform(post(API + "/" + session.getId() + "/start")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk());

        session.setStatus("ACTIVE");

        await().atMost(20, TimeUnit.SECONDS)
                .until(() -> repRepository.findAllBySessionId(session.getId()).size() >= 2);

        List<Rep> reps = repRepository.findAllBySessionId(session.getId());
        List<Integer> repNumbers = reps.stream()
                .map(Rep::getRepNumber)
                .sorted()
                .toList();

        assertEquals(1, repNumbers.get(0));
        assertEquals(2, repNumbers.get(1));
    }
}
