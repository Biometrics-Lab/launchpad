package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.ReportRepository;
import com.biolab.launchpad.internal.repository.model.Report;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.biolab.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@DisplayName("ReportController Integration Tests")
class ReportControllerIntegrationTest {

    private static final String API = "/api/v1/report-configs";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReportRepository reportRepository;

    @AfterEach
    void tearDown() {
        reportRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /reports — list")
    class ListTests {
        @Test
        @DisplayName("returns presets + DB records for a reportType")
        void listAll() throws Exception {
            reportRepository.save(Report.builder()
                .name("My Config")
                .reportType("player-assessment")
                .config("{\"granularity\":\"OVERALL\"}")
                .build());

            String json = mvc.perform(get(API + "?reportType=player-assessment")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode response = objectMapper.readTree(json);
            assertThat(response.isArray()).isTrue();
            // 3 presets + 1 DB record
            assertThat(response.size()).isEqualTo(4);
            // first item is a preset
            assertThat(response.get(0).get("preset").asBoolean()).isTrue();
            assertThat(response.get(0).get("id").isNull()).isTrue();
        }
    }

    @Nested
    @DisplayName("GET /reports/{name}")
    class GetByNameTests {
        @Test
        @DisplayName("returns preset by name")
        void getPreset() throws Exception {
            String json = mvc.perform(get(API + "/Column Chart")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode node = objectMapper.readTree(json);
            assertEquals("Column Chart", node.get("name").asText());
            assertThat(node.get("preset").asBoolean()).isTrue();
        }

        @Test
        @DisplayName("returns DB config by name")
        void getDbConfig() throws Exception {
            reportRepository.save(Report.builder()
                .name("Custom Config")
                .reportType("player-assessment")
                .config("{\"granularity\":\"PER_SESSION\"}")
                .build());

            String json = mvc.perform(get(API + "/Custom Config")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode node = objectMapper.readTree(json);
            assertEquals("Custom Config", node.get("name").asText());
            assertThat(node.get("preset").asBoolean()).isFalse();
            assertEquals("PER_SESSION", node.get("config").get("granularity").asText());
        }

        @Test
        @DisplayName("returns 404 for unknown name")
        void getNotFound() throws Exception {
            mvc.perform(get(API + "/Unknown Name")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /reports")
    class CreateTests {
        @Test
        @DisplayName("creates a new DB config")
        void create() throws Exception {
            String request = """
                {
                  "name": "My Report",
                  "reportType": "player-assessment",
                  "config": { "granularity": "OVERALL", "panels": [] }
                }
                """;

            String json = mvc.perform(post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode node = objectMapper.readTree(json);
            assertThat(node.get("id").asInt()).isPositive();
            assertEquals("My Report", node.get("name").asText());
            assertThat(node.get("preset").asBoolean()).isFalse();
        }

        @Test
        @DisplayName("returns 409 when name conflicts with preset")
        void conflictWithPreset() throws Exception {
            String request = """
                {
                  "name": "Column Chart",
                  "reportType": "player-assessment",
                  "config": {}
                }
                """;
            mvc.perform(post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("returns 409 when name already exists in DB")
        void conflictWithExisting() throws Exception {
            reportRepository.save(Report.builder()
                .name("Duplicate")
                .reportType("player-assessment")
                .config("{}")
                .build());

            String request = """
                { "name": "Duplicate", "reportType": "player-assessment", "config": {} }
                """;
            mvc.perform(post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("returns 422 when name is blank")
        void validationError() throws Exception {
            String request = """
                { "name": "", "reportType": "player-assessment", "config": {} }
                """;
            mvc.perform(post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /reports/{name}")
    class UpdateTests {
        @Test
        @DisplayName("updates existing DB config")
        void update() throws Exception {
            reportRepository.save(Report.builder()
                .name("To Update")
                .reportType("player-assessment")
                .config("{\"granularity\":\"OVERALL\"}")
                .build());

            String request = """
                {
                  "name": "To Update",
                  "reportType": "player-assessment",
                  "config": { "granularity": "PER_SESSION" }
                }
                """;

            String json = mvc.perform(put(API + "/To Update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            assertEquals("PER_SESSION", objectMapper.readTree(json).get("config").get("granularity").asText());
        }

        @Test
        @DisplayName("returns 409 when trying to update preset")
        void cannotUpdatePreset() throws Exception {
            String request = """
                { "name": "Column Chart", "reportType": "player-assessment", "config": {} }
                """;
            mvc.perform(put(API + "/Column Chart")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /reports/{name}")
    class DeleteTests {
        @Test
        @DisplayName("deletes existing DB config")
        void deleteExisting() throws Exception {
            reportRepository.save(Report.builder()
                .name("To Delete")
                .reportType("player-assessment")
                .config("{}")
                .build());

            mvc.perform(delete(API + "/To Delete")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk());

            assertFalse(reportRepository.findByName("To Delete").isPresent());
        }

        @Test
        @DisplayName("returns 409 when trying to delete preset")
        void cannotDeletePreset() throws Exception {
            mvc.perform(delete(API + "/Column Chart")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("returns 404 for unknown name")
        void notFound() throws Exception {
            mvc.perform(delete(API + "/Unknown")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isNotFound());
        }
    }
}
