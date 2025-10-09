package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.model.Metric;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.repository.model.RepMetric;
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
@DisplayName("Rep metric Integration Tests")
class RepMetricControllerIntegrationTest {

    private static final String API = "/api/v1/repMetrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RepMetricRepository repMetricRepository;

    @Autowired
    EntityFactory factory;

    Rep    rep;
    Metric metric;

    @BeforeEach
    void setUp() {
        rep    = factory.createRep("eminem");
        metric = factory.createMetric("wide");
    }

    @AfterEach
    void tearDown() {
        repMetricRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /rep_metrics -> creates and returns the new Session")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "repId"        : %d,
                                    "metricId"     : %d,
                                    "value"        : 3
                                }
                            """.formatted(rep.getId(), metric.getId());

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
                                        "repId"        : %d,
                                        "metricId"     : %d,
                                        "value"        : 3
                                      }
                                    """.formatted(sessionId, rep.getId(), metric.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /rep_metrics with validation message -> returns 422")
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
                                "message"      : "Validation failed: metricId: RepMetric metricId cannot be null, and repId: RepMetric repId cannot be null"
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
        @DisplayName("GET /rep_metrics -> returns all rep_metrics")
        void getAll() throws Exception {

            RepMetric repMetric1 = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .metricId(metric.getId())
                    .value(3)
                    .build());

            RepMetric repMetric2 = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .metricId(metric.getId())
                    .value(7)
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
                        "repId"        : %d,
                        "metricId"     : %d,
                        "value"        : 3
                   },
                    {
                        "id"           : %d,
                        "repId"        : %d,
                        "metricId"     : %d,
                        "value"        : 7
                    }
                ]
                """.formatted(repMetric1.getId(), rep.getId(), metric.getId(), repMetric2.getId(),rep.getId(), metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /rep_metrics/{id} -> returns session by ID")
        void getById() throws Exception {

            RepMetric repMetric = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .metricId(metric.getId())
                    .value(3)
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + repMetric.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                     {
                                        "id"            : %d,
                                         "repId"        : %d,
                                         "metricId"     : %d,
                                         "value"        : 3
                                     }
                                    """.formatted(repMetric.getId(), rep.getId(), metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /rep_metrics/{id} with unknown ID -> returns 404")
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
                    "message": "RepMetric not found by id: 999999"
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
        @DisplayName("PUT /rep_metrics -> updates and returns the Session")
        void update() throws Exception {
            RepMetric original = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .metricId(metric.getId())
                    .value(3)
                    .build());

           String updateRequest = """
                                {
                                   "id"           : %d,
                                   "repId"        : %d,
                                   "metricId"     : %d,
                                   "value"        : 6
                                }
                                """.formatted(original.getId(), rep.getId(), metric.getId());

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
                                    "id"           : %d,
                                    "repId"        : %d,
                                    "metricId"     : %d,
                                    "value"        : 6
                                 }
                                """.formatted(original.getId(), rep.getId(), metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);
        }

        @Test
        @DisplayName("PUT /rep_metrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"           : 999999,
                                     "repId"        : %d,
                                     "metricId"     : %d
                                 }
                                 """.formatted(rep.getId(), metric.getId());

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
                    "message": "RepMetricService. Could not update RepMetric by id: 999999"
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
        @DisplayName("DELETE /rep_metrics/{id} -> deletes the Session")
        void delete() throws Exception {
            RepMetric repMetric = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .metricId(metric.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + repMetric.getId())
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

            assertFalse(repMetricRepository.existsById(repMetric.getId()));
        }

        @Test
        @DisplayName("DELETE /rep_metrics/{id} with unknown ID -> returns 404")
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
                    "message": "RepMetricService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}