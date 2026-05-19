package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.SessionMetricRepository;
import com.biolab.launchpad.internal.repository.model.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Session metric Integration Tests")
class SessionMetricControllerIntegrationTest {

    private static final String API = "/api/v1/sessionMetrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SessionMetricRepository sessionMetricRepository;

    @Autowired
    EntityFactory factory;

    Session session;
    ConditionalMetric conditionalMetric;

    @BeforeEach
    void setUp() {
        session = factory.createSession1();
        conditionalMetric = factory.createConditionalMetric();
    }

    @AfterEach
    void tearDown() {
        sessionMetricRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /sessionMetrics -> creates and returns the new SessionMetric")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "session1Id"          : %d,
                                    "conditionalMetricId" : %d,
                                    "minValue"            : 1,
                                    "maxValue"            : 2,
                                    "avgValue"            : 3
                                }
                            """.formatted(session.getId(), conditionalMetric.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int id = responseNode.get("id").asInt();
            assertThat(id).isPositive();

            String expectedResponse =
                                    """
                                      {
                                        "id"                  : %d,
                                        "session1Id"          : %d,
                                        "conditionalMetricId" : %d,
                                        "minValue"            : 1,
                                        "maxValue"            : 2,
                                        "avgValue"            : 3
                                      }
                                    """.formatted(id, session.getId(), conditionalMetric.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /sessionMetrics with validation error -> returns 422")
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
                                "status"  : 422,
                                "message" : "Validation failed: conditionalMetricId: SessionMetric conditionalMetricId cannot be null, and session1Id: SessionMetric sessionId cannot be null"
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
        @DisplayName("GET /sessionMetrics -> returns all sessionMetrics")
        void getAll() throws Exception {

            SessionMetric sessionMetric1 = sessionMetricRepository.save(SessionMetric.builder()
                    .session1Id(session.getId())
                    .conditionalMetricId(conditionalMetric.getId())
                    .minValue(1)
                    .maxValue(2)
                    .avgValue(3)
                    .build());

            SessionMetric sessionMetric2 = sessionMetricRepository.save(SessionMetric.builder()
                    .session1Id(session.getId())
                    .conditionalMetricId(conditionalMetric.getId())
                    .minValue(5)
                    .maxValue(6)
                    .avgValue(7)
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
                        "id"                  : %d,
                        "session1Id"          : %d,
                        "conditionalMetricId" : %d,
                        "minValue"            : 1,
                        "maxValue"            : 2,
                        "avgValue"            : 3
                   },
                    {
                        "id"                  : %d,
                        "session1Id"          : %d,
                        "conditionalMetricId" : %d,
                        "minValue"            : 5,
                        "maxValue"            : 6,
                        "avgValue"            : 7
                    }
                ]
                """.formatted(sessionMetric1.getId(), session.getId(), conditionalMetric.getId(),
                              sessionMetric2.getId(), session.getId(), conditionalMetric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /sessionMetrics/{id} -> returns sessionMetric by ID")
        void getById() throws Exception {

            SessionMetric sessionMetric = sessionMetricRepository.save(SessionMetric.builder()
                    .session1Id(session.getId())
                    .conditionalMetricId(conditionalMetric.getId())
                    .minValue(1)
                    .maxValue(2)
                    .avgValue(3)
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + sessionMetric.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                     {
                                        "id"                  : %d,
                                        "session1Id"          : %d,
                                        "conditionalMetricId" : %d,
                                        "minValue"            : 1,
                                        "maxValue"            : 2,
                                        "avgValue"            : 3
                                     }
                                    """.formatted(sessionMetric.getId(), session.getId(), conditionalMetric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /sessionMetrics/{id} with unknown ID -> returns 404")
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
                    "message": "SessionMetric not found by id: 999999"
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
        @DisplayName("PUT /sessionMetrics -> updates and returns the SessionMetric")
        void update() throws Exception {
            SessionMetric original = sessionMetricRepository.save(SessionMetric.builder()
                    .session1Id(session.getId())
                    .conditionalMetricId(conditionalMetric.getId())
                    .minValue(1)
                    .maxValue(2)
                    .avgValue(3)
                    .build());

           String updateRequest = """
                                {
                                   "id"                  : %d,
                                   "session1Id"          : %d,
                                   "conditionalMetricId" : %d,
                                   "minValue"            : 4,
                                   "maxValue"            : 5,
                                   "avgValue"            : 6
                                }
                                """.formatted(original.getId(), session.getId(), conditionalMetric.getId());

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
                                    "id"                  : %d,
                                    "session1Id"          : %d,
                                    "conditionalMetricId" : %d,
                                    "minValue"            : 4,
                                    "maxValue"            : 5,
                                    "avgValue"            : 6
                                 }
                                """.formatted(original.getId(), session.getId(), conditionalMetric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);
        }

        @Test
        @DisplayName("PUT /sessionMetrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"                  : 999999,
                                     "session1Id"          : %d,
                                     "conditionalMetricId" : %d
                                 }
                                 """.formatted(session.getId(), conditionalMetric.getId());

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
                    "message": "SessionMetricService. Could not update SessionMetric by id: 999999"
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
        @DisplayName("DELETE /sessionMetrics/{id} -> deletes the SessionMetric")
        void delete() throws Exception {
            SessionMetric sessionMetric = sessionMetricRepository.save(SessionMetric.builder()
                    .session1Id(session.getId())
                    .conditionalMetricId(conditionalMetric.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + sessionMetric.getId())
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

            assertFalse(sessionMetricRepository.existsById(sessionMetric.getId()));
        }

        @Test
        @DisplayName("DELETE /sessionMetrics/{id} with unknown ID -> returns 404")
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
                    "message": "SessionMetricService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}
