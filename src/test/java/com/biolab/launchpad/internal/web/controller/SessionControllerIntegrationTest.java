package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.Assessment;
import com.biolab.launchpad.internal.repository.model.Session;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Timestamp;
import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Session Integration Tests")
class SessionControllerIntegrationTest {

    private static final String API = "/api/v1/sessions";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SessionRepository sessionRepository;

    @Autowired
    EntityFactory factory;

    Assessment assessment;

    @BeforeEach
    void setUp() {
        assessment   = factory.createAssessment();
    }

    @AfterEach
    void tearDown() {
        sessionRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /sessions -> creates and returns the new Session")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "assessmentId" : %d,
                                    "startTime"    : "2025-10-02T14:45:00.000+00:00"
                                }
                            """.formatted(assessment.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int sessionId = responseNode.get("id").asInt();
            assertThat(sessionId).isPositive();


            String expectedResponse =
                                    """
                                      {
                                        "id"           : %d,
                                        "assessmentId" : %d,
                                        "startTime"    : "2025-10-02T14:45:00.000+00:00",
                                        "status"       : null
                                      }
                                    """.formatted(sessionId, assessment.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /sessions with validation message -> returns 422")
        void createValidationError() throws Exception {
            String request =
                            """
                                {
                                    "description" : ""
                                }
                            """;

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isBadRequest())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            String expectedResponse =
                    """
                            {
                                "status"       : 422,
                                "message"      : "Validation failed: assessmentId: Session assessmentId cannot be null, and startTime: Session startTime cannot be null"
                            }
                            """;

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }
    }

    @Nested
    @DisplayName("Read")
    class ReadTests {

        @Test
        @DisplayName("GET /sessions -> returns all sessions")
        void getAll() throws Exception {

            ZonedDateTime zdt = ZonedDateTime.of(2025, 10, 2, 15, 45, 10, 0, ZoneOffset.UTC);

            Session session11 = sessionRepository.save(Session.builder()
                    .assessmentId(assessment.getId())
                    .startTime(Timestamp.from(zdt.toInstant()))
                    .build());

            Session session12 = sessionRepository.save(Session.builder()
                    .assessmentId(assessment.getId())
                    .startTime(Timestamp.from(zdt.toInstant()))
                    .build());

            String jsonResponse = mvc.perform(
                            get(API)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                [
                    {
                        "id"           : %d,
                        "assessmentId" : %d,
                        "startTime"    : "2025-10-02T15:45:10.000+00:00",
                        "status"       : null
                    },
                    {
                        "id"            : %d,
                        "assessmentId"  : %d,
                        "startTime"     : "2025-10-02T15:45:10.000+00:00",
                        "status"        : null
                    }
                ]
                """.formatted(session11.getId(), assessment.getId(), session12.getId(),assessment.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /sessions/{id} -> returns session by ID")
        void getById() throws Exception {

            ZonedDateTime zdt = ZonedDateTime.of(2025, 10, 2, 15, 45, 10, 0, ZoneOffset.UTC);

            Session session = sessionRepository.save(Session.builder()
                    .assessmentId(assessment.getId())
                    .startTime(Timestamp.from(zdt.toInstant()))
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + session.getId())
                            .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                     {
                                        "id"             : %d,
                                         "assessmentId"  : %d,
                                         "startTime"   : "2025-10-02T15:45:10.000+00:00",
                                         "status"      : null
                                     }
                                    """.formatted(session.getId(), assessment.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /sessions/{id} with unknown ID -> returns 404")
        void getByIdValidationError() throws Exception {
            String jsonResponse = mvc.perform(
                            get(API + "/999999")
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "status" : 404,
                    "message": "Session not found by id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("PUT /sessions -> updates and returns the Session")
        void update() throws Exception {
            Session original = sessionRepository.save(Session.builder()
                    .assessmentId(assessment.getId())
                    .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                    .build());

            String updateRequest = """
                                {
                                    "id"            : %d,
                                    "assessmentId"  : %d,
                                    "startTime"     : "2026-10-02T14:45:00.000+00:00"
                                 }
                                """.formatted(original.getId(), assessment.getId());

            String jsonResponse = mvc.perform(
                            put(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(updateRequest)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            String expectedResponse = """
                                {
                                    "id"            : %d,
                                    "assessmentId"  : %d,
                                    "startTime"     : "2026-10-02T14:45:00.000+00:00",
                                    "status"        : null
                                 }
                                """.formatted(original.getId(), assessment.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

        }

        @Test
        @DisplayName("PUT /sessions with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"            : 999999,
                                     "assessmentId"  : %d,
                                     "startTime"     : "2025-10-02T14:45:00.000+00:00"
                                 }
                                 """.formatted(assessment.getId());

            String jsonResponse = mvc.perform(
                            put(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(updateRequest)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();


            String expectedResponse = """
                {
                    "status" : 404,
                    "message": "SessionService. Could not update Session by id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);

        }

    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {


        @Test
        @DisplayName("DELETE /sessions/{id} -> deletes the Session")
        void delete() throws Exception {
            Session session = sessionRepository.save(Session.builder()
                    .assessmentId(assessment.getId())
                    .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + session.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "status" : 200,
                    "message": "Success"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);

            assertFalse(sessionRepository.existsById(session.getId()));
        }

        @Test
        @DisplayName("DELETE /sessions/{id} with unknown ID -> returns 404")
        void deleteValidationError() throws Exception {
            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/999999")
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "status" : 404,
                    "message": "SessionService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}