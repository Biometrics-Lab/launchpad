package com.biolab.launchpad.internal.web.controller.reports;

import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import com.biolab.launchpad.internal.web.controller.EntityFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.biolab.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("PlayerAssessmentReportController Integration Tests")
class PlayerAssessmentReportControllerIntegrationTest {

    private static final String API = "/api/v1/reports/player-assessment";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EntityFactory factory;
    @Autowired private AssessmentMetricRepository assessmentMetricRepository;
    @Autowired private SessionMetricRepository sessionMetricRepository;
    @Autowired private RepMetricRepository repMetricRepository;
    @Autowired private ReportRepository reportRepository;

    @AfterEach
    void tearDown() {
        reportRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("404 handling")
    class NotFoundTests {
        @Test
        @DisplayName("returns 404 when assessment does not exist")
        void assessmentNotFound() throws Exception {
            mvc.perform(get(API + "/999999").with(httpBasic("biolab", "biolab")))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("OVERALL granularity")
    class OverallTests {
        @Test
        @DisplayName("returns one column with assessment metrics")
        void overallReturnsColumns() throws Exception {
            Assessment assessment = factory.createAssessment();
            ConditionalMetric cm = factory.createConditionalMetric();
            assessmentMetricRepository.save(AssessmentMetric.builder()
                .assessmentId(assessment.getId())
                .conditionalMetricId(cm.getId())
                .dataSourceId(factory.createDataSource(cm.getMetricId()).getId())
                .minValue(BigDecimal.valueOf(70.0))
                .maxValue(BigDecimal.valueOf(110.0))
                .avgValue(BigDecimal.valueOf(90.0))
                .sessionCount(5)
                .build());

            String json = mvc.perform(get(API + "/" + assessment.getId())
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode response = objectMapper.readTree(json);
            assertThat(response.get("columns").size()).isEqualTo(1);
            assertEquals("assessment", response.get("columns").get(0).get("type").asText());

            assertThat(response.get("rows").size()).isEqualTo(1);
            JsonNode cell = response.get("rows").get(0).get("cells").get(0);
            assertEquals(70.0, cell.get("min").asDouble(), 0.001);
            assertEquals(110.0, cell.get("max").asDouble(), 0.001);
            assertEquals(90.0, cell.get("avg").asDouble(), 0.001);
            assertEquals(5, cell.get("count").asInt());
        }

        @Test
        @DisplayName("empty state — no assessment metrics returns empty rows")
        void overallEmptyState() throws Exception {
            Assessment assessment = factory.createAssessment();

            String json = mvc.perform(get(API + "/" + assessment.getId())
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode response = objectMapper.readTree(json);
            assertThat(response.get("columns").size()).isEqualTo(1);
            assertThat(response.get("rows").size()).isEqualTo(0);
        }

        @Test
        @DisplayName("uses named DB config when configName matches saved config")
        void usesDbConfig() throws Exception {
            reportRepository.save(Report.builder()
                .name("My Config")
                .reportType("player-assessment")
                .config("{\"granularity\":\"OVERALL\",\"filters\":{\"conditionalMetricIds\":[]},\"panels\":[]}")
                .build());
            Assessment assessment = factory.createAssessment();

            mvc.perform(get(API + "/" + assessment.getId() + "?configName=My Config")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("uses preset when configName matches preset")
        void usesPreset() throws Exception {
            Assessment assessment = factory.createAssessment();

            mvc.perform(get(API + "/" + assessment.getId() + "?configName=Line Chart")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("PER_SESSION granularity")
    class PerSessionTests {
        @Test
        @DisplayName("returns session columns with session metric cells")
        void perSessionColumns() throws Exception {
            reportRepository.save(Report.builder()
                .name("Session Config")
                .reportType("player-assessment")
                .config("{\"granularity\":\"PER_SESSION\",\"filters\":{\"conditionalMetricIds\":[]},\"panels\":[]}")
                .build());

            Assessment assessment = factory.createAssessment();
            ConditionalMetric cm = factory.createConditionalMetric();
            Session session = factory.createSessionForAssessment(assessment.getId());
            sessionMetricRepository.save(SessionMetric.builder()
                .sessionId(session.getId())
                .conditionalMetricId(cm.getId())
                .minValue(60.0)
                .maxValue(100.0)
                .avgValue(80.0)
                .build());

            String json = mvc.perform(get(API + "/" + assessment.getId() + "?configName=Session Config")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode response = objectMapper.readTree(json);
            assertThat(response.get("columns").size()).isEqualTo(1);
            assertEquals("session", response.get("columns").get(0).get("type").asText());

            JsonNode cell = response.get("rows").get(0).get("cells").get(0);
            assertEquals(60.0, cell.get("min").asDouble(), 0.001);
            assertEquals(100.0, cell.get("max").asDouble(), 0.001);
            assertEquals(80.0, cell.get("avg").asDouble(), 0.001);
            assertEquals(1, cell.get("count").asInt());
        }
    }

    @Nested
    @DisplayName("PER_REP granularity")
    class PerRepTests {
        @Test
        @DisplayName("returns rep columns with min=max=avg=value and count=1")
        void perRepColumns() throws Exception {
            reportRepository.save(Report.builder()
                .name("Rep Config")
                .reportType("player-assessment")
                .config("{\"granularity\":\"PER_REP\",\"filters\":{\"conditionalMetricIds\":[]},\"panels\":[]}")
                .build());

            Assessment assessment = factory.createAssessment();
            ConditionalMetric cm = factory.createConditionalMetric();
            Rep rep = factory.createRepForAssessment(assessment.getId());

            repMetricRepository.save(RepMetric.builder()
                .repId(rep.getId())
                .conditionalMetricId(cm.getId())
                .dataSourceId(factory.createDataSource(cm.getMetricId()).getId())
                .value(BigDecimal.valueOf(95.5))
                .build());

            String json = mvc.perform(get(API + "/" + assessment.getId() + "?configName=Rep Config")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode response = objectMapper.readTree(json);
            assertThat(response.get("columns").size()).isEqualTo(1);
            assertEquals("rep", response.get("columns").get(0).get("type").asText());

            JsonNode cell = response.get("rows").get(0).get("cells").get(0);
            assertEquals(95.5, cell.get("avg").asDouble(), 0.001);
            assertEquals(cell.get("min").asDouble(), cell.get("max").asDouble(), 0.001);
            assertEquals(cell.get("min").asDouble(), cell.get("avg").asDouble(), 0.001);
            assertEquals(1, cell.get("count").asInt());
        }
    }
}
