package com.biolab.launchpad.internal.web.controller;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.SessionStartedEvent;
import com.biolab.common.SessionStoppedEvent;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({TestcontainersConfiguration.class, EntityFactory.class})
@RecordApplicationEvents
@DisplayName("Session Workflow Integration Tests")
class SessionWorkflowIntegrationTest {

    private static final String API = "/api/v1/sessions";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SessionRepository sessionRepository;
    @Autowired EntityFactory entityFactory;
    @Autowired ApplicationEvents events;

    @AfterEach
    void tearDown() {
        entityFactory.cleanup();
    }

    @Nested
    @DisplayName("Start Session")
    class StartTests {

        @Test
        @DisplayName("POST /sessions/{id}/start -> sets status ACTIVE and publishes SessionStartedEvent")
        void startHappyPath() throws Exception {
            var session = entityFactory.createSession();

            String jsonResponse = mvc.perform(
                            post(API + "/" + session.getId() + "/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals("ACTIVE", responseNode.get("status").asText());

            var saved = sessionRepository.findById(session.getId()).orElseThrow();
            assertEquals("ACTIVE", saved.getStatus());

            assertEquals(1, events.stream(SessionStartedEvent.class).count());
        }

        @Test
        @DisplayName("POST /sessions/{id}/start on already ACTIVE session -> 409")
        void startAlreadyActive() throws Exception {
            var session = entityFactory.createActiveSession();

            String jsonResponse = mvc.perform(
                            post(API + "/" + session.getId() + "/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals(409, responseNode.get("status").asInt());
            assertEquals("Session already started", responseNode.get("message").asText());
        }

        @Test
        @DisplayName("POST /sessions/{id}/start on COMPLETE session -> 409")
        void startAlreadyComplete() throws Exception {
            var session = entityFactory.createSession();
            session.setStatus("COMPLETE");
            sessionRepository.save(session);

            String jsonResponse = mvc.perform(
                            post(API + "/" + session.getId() + "/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals(409, responseNode.get("status").asInt());
            assertEquals("Session already started", responseNode.get("message").asText());
        }

        @Test
        @DisplayName("POST /sessions/{id}/start with unknown id -> 404")
        void startNotFound() throws Exception {
            String jsonResponse = mvc.perform(
                            post(API + "/999999/start")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals(404, responseNode.get("status").asInt());
        }
    }

    @Nested
    @DisplayName("Stop Session")
    class StopTests {

        @Test
        @DisplayName("POST /sessions/{id}/stop -> sets status COMPLETE and publishes SessionStoppedEvent")
        void stopHappyPath() throws Exception {
            var session = entityFactory.createActiveSession();

            String jsonResponse = mvc.perform(
                            post(API + "/" + session.getId() + "/stop")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals("COMPLETE", responseNode.get("status").asText());

            var saved = sessionRepository.findById(session.getId()).orElseThrow();
            assertEquals("COMPLETE", saved.getStatus());

            assertEquals(1, events.stream(SessionStoppedEvent.class).count());
        }

        @Test
        @DisplayName("POST /sessions/{id}/stop on non-ACTIVE session -> 409")
        void stopNotActive() throws Exception {
            var session = entityFactory.createSession();

            String jsonResponse = mvc.perform(
                            post(API + "/" + session.getId() + "/stop")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isConflict())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            assertEquals(409, responseNode.get("status").asInt());
            assertEquals("Session is not active", responseNode.get("message").asText());
        }

        @Test
        @DisplayName("POST /sessions/{id}/stop with unknown id -> 404")
        void stopNotFound() throws Exception {
            mvc.perform(
                            post(API + "/999999/stop")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound());
        }
    }
}
