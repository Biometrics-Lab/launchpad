package com.biolab.launchpad.internal.web.controller;

import com.biolab.TestcontainersConfiguration;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.repository.model.RepMetric;
import com.biolab.launchpad.internal.repository.model.Session;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@DisplayName("Player Metrics Integration Tests")
class PlayerMetricsIntegrationTest {

    private static final String SESSIONS_API = "/api/v1/sessions";
    private static final String PLAYERS_API  = "/api/v1/players";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired EntityFactory entityFactory;
    @Autowired SessionRepository sessionRepository;
    @Autowired RepRepository repRepository;
    @Autowired RepMetricRepository repMetricRepository;

    @AfterEach
    void tearDown() {
        entityFactory.cleanup();
    }

    @Test
    @DisplayName("Player with no assessments -> returns empty overall and assessments")
    void noAssessments() throws Exception {
        var player = entityFactory.createPlayer("TestPlayer");

        String json = mvc.perform(get(PLAYERS_API + "/" + player.getId() + "/metrics")
                        .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        assertTrue(root.get("overall").isEmpty());
        assertTrue(root.get("assessments").isEmpty());
    }

    @Test
    @DisplayName("One assessment, non-negated metric -> correct min/max/avg in assessments and overall")
    void oneAssessmentNonNegated() throws Exception {
        var player     = entityFactory.createPlayer("TestPlayer");
        var cm         = entityFactory.createConditionalMetric();
        var assessment = entityFactory.createAssessment(player.getId());
        entityFactory.createAssessmentMetric(assessment.getId(), cm.getId());

        var session = sessionRepository.save(Session.builder()
                .assessmentId(assessment.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 0)))
                .status("ACTIVE").build());
        var rep1 = repRepository.save(Rep.builder().sessionId(session.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1))).build());
        var rep2 = repRepository.save(Rep.builder().sessionId(session.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 2))).build());
        repMetricRepository.save(RepMetric.builder().repId(rep1.getId())
                .conditionalMetricId(cm.getId()).value(10.0).build());
        repMetricRepository.save(RepMetric.builder().repId(rep2.getId())
                .conditionalMetricId(cm.getId()).value(20.0).build());
        mvc.perform(post(SESSIONS_API + "/" + session.getId() + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic("biolab", "biolab"))).andExpect(status().isOk());

        String json = mvc.perform(get(PLAYERS_API + "/" + player.getId() + "/metrics")
                        .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root   = objectMapper.readTree(json);
        JsonNode metric = root.get("assessments").get(0).get("metrics").get(0);
        assertEquals(10.0, metric.get("minValue").asDouble(), 0.001);
        assertEquals(20.0, metric.get("maxValue").asDouble(), 0.001);
        assertEquals(15.0, metric.get("avgValue").asDouble(), 0.001);
        assertFalse(metric.get("negated").asBoolean());

        JsonNode overall = root.get("overall").get(0);
        assertEquals(10.0, overall.get("minValue").asDouble(), 0.001);
        assertEquals(20.0, overall.get("maxValue").asDouble(), 0.001);
        assertEquals(15.0, overall.get("avgValue").asDouble(), 0.001);
    }

    @Test
    @DisplayName("One assessment, negated metric -> min/max swapped in assessments and overall")
    void oneAssessmentNegated() throws Exception {
        var player     = entityFactory.createPlayer("TestPlayer");
        var cm         = entityFactory.createNegatedConditionalMetric();
        var assessment = entityFactory.createAssessment(player.getId());
        entityFactory.createAssessmentMetric(assessment.getId(), cm.getId());

        var session = sessionRepository.save(Session.builder()
                .assessmentId(assessment.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 0)))
                .status("ACTIVE").build());
        var rep1 = repRepository.save(Rep.builder().sessionId(session.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1))).build());
        var rep2 = repRepository.save(Rep.builder().sessionId(session.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 2))).build());
        repMetricRepository.save(RepMetric.builder().repId(rep1.getId())
                .conditionalMetricId(cm.getId()).value(10.0).build());
        repMetricRepository.save(RepMetric.builder().repId(rep2.getId())
                .conditionalMetricId(cm.getId()).value(20.0).build());
        mvc.perform(post(SESSIONS_API + "/" + session.getId() + "/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic("biolab", "biolab"))).andExpect(status().isOk());

        String json = mvc.perform(get(PLAYERS_API + "/" + player.getId() + "/metrics")
                        .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root   = objectMapper.readTree(json);
        JsonNode metric = root.get("assessments").get(0).get("metrics").get(0);
        assertEquals(20.0, metric.get("minValue").asDouble(), 0.001);
        assertEquals(10.0, metric.get("maxValue").asDouble(), 0.001);
        assertEquals(15.0, metric.get("avgValue").asDouble(), 0.001);
        assertTrue(metric.get("negated").asBoolean());

        JsonNode overall = root.get("overall").get(0);
        assertEquals(20.0, overall.get("minValue").asDouble(), 0.001);
        assertEquals(10.0, overall.get("maxValue").asDouble(), 0.001);
        assertEquals(15.0, overall.get("avgValue").asDouble(), 0.001);
    }

    @Test
    @DisplayName("Two assessments, same conditionalMetricId -> overall rolls up correctly")
    void twoAssessmentsSameMetric() throws Exception {
        var player = entityFactory.createPlayer("TestPlayer");
        var cm     = entityFactory.createConditionalMetric();

        // Assessment 1: values [10.0, 20.0] -> min=10, max=20, avg=15
        var a1 = entityFactory.createAssessment(player.getId());
        entityFactory.createAssessmentMetric(a1.getId(), cm.getId());
        var s1  = sessionRepository.save(Session.builder().assessmentId(a1.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 0))).status("ACTIVE").build());
        var r1a = repRepository.save(Rep.builder().sessionId(s1.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1))).build());
        var r1b = repRepository.save(Rep.builder().sessionId(s1.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 2))).build());
        repMetricRepository.save(RepMetric.builder().repId(r1a.getId()).conditionalMetricId(cm.getId()).value(10.0).build());
        repMetricRepository.save(RepMetric.builder().repId(r1b.getId()).conditionalMetricId(cm.getId()).value(20.0).build());
        mvc.perform(post(SESSIONS_API + "/" + s1.getId() + "/stop")
                .contentType(MediaType.APPLICATION_JSON).with(httpBasic("biolab", "biolab"))).andExpect(status().isOk());

        // Assessment 2: values [30.0, 40.0] -> min=30, max=40, avg=35
        var a2 = entityFactory.createAssessment(player.getId());
        entityFactory.createAssessmentMetric(a2.getId(), cm.getId());
        var s2  = sessionRepository.save(Session.builder().assessmentId(a2.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 46, 0))).status("ACTIVE").build());
        var r2a = repRepository.save(Rep.builder().sessionId(s2.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 46, 1))).build());
        var r2b = repRepository.save(Rep.builder().sessionId(s2.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 46, 2))).build());
        repMetricRepository.save(RepMetric.builder().repId(r2a.getId()).conditionalMetricId(cm.getId()).value(30.0).build());
        repMetricRepository.save(RepMetric.builder().repId(r2b.getId()).conditionalMetricId(cm.getId()).value(40.0).build());
        mvc.perform(post(SESSIONS_API + "/" + s2.getId() + "/stop")
                .contentType(MediaType.APPLICATION_JSON).with(httpBasic("biolab", "biolab"))).andExpect(status().isOk());

        String json = mvc.perform(get(PLAYERS_API + "/" + player.getId() + "/metrics")
                        .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode root = objectMapper.readTree(json);
        assertEquals(2, root.get("assessments").size());

        JsonNode overall = root.get("overall").get(0);
        assertEquals(10.0, overall.get("minValue").asDouble(), 0.001);
        assertEquals(40.0, overall.get("maxValue").asDouble(), 0.001);
        assertEquals(25.0, overall.get("avgValue").asDouble(), 0.001);
    }

    @Test
    @DisplayName("Player not found -> 404")
    void playerNotFound() throws Exception {
        mvc.perform(get(PLAYERS_API + "/999999/metrics")
                        .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isNotFound());
    }
}
