package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
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

    Rep               rep;
    ConditionalMetric conditionalMetric;

    @BeforeEach
    void setUp() {
        rep               = factory.createRep();
        conditionalMetric = factory.createConditionalMetric();
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
        @DisplayName("POST /repMetrics -> creates and returns the new RepMetric")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "repId"               : %d,
                                    "conditionalMetricId" : %d,
                                    "value"               : 3
                                }
                            """.formatted(rep.getId(), conditionalMetric.getId());

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
                                        "repId"               : %d,
                                        "conditionalMetricId" : %d,
                                        "dataSourceId"        : null,
                                        "value"               : 3
                                      }
                                    """.formatted(id, rep.getId(), conditionalMetric.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /repMetrics with validation error -> returns 422")
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
                                "message" : "Validation failed: conditionalMetricId: RepMetric conditionalMetricId cannot be null, and repId: RepMetric repId cannot be null"
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
        @DisplayName("GET /repMetrics -> returns all repMetrics")
        void getAll() throws Exception {

            RepMetric repMetric1 = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .conditionalMetricId(conditionalMetric.getId())
                    .value(3)
                    .build());

            RepMetric repMetric2 = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .conditionalMetricId(conditionalMetric.getId())
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
                        "id"                  : %d,
                        "repId"               : %d,
                        "conditionalMetricId" : %d,
                        "dataSourceId"        : null,
                        "value"               : 3
                   },
                    {
                        "id"                  : %d,
                        "repId"               : %d,
                        "conditionalMetricId" : %d,
                        "dataSourceId"        : null,
                        "value"               : 7
                    }
                ]
                """.formatted(repMetric1.getId(), rep.getId(), conditionalMetric.getId(),
                              repMetric2.getId(), rep.getId(), conditionalMetric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /repMetrics/{id} -> returns repMetric by ID")
        void getById() throws Exception {

            RepMetric repMetric = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .conditionalMetricId(conditionalMetric.getId())
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
                                        "id"                  : %d,
                                        "repId"               : %d,
                                        "conditionalMetricId" : %d,
                                        "dataSourceId"        : null,
                                        "value"               : 3
                                     }
                                    """.formatted(repMetric.getId(), rep.getId(), conditionalMetric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /repMetrics/{id} with unknown ID -> returns 404")
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
        @DisplayName("PUT /repMetrics -> updates and returns the RepMetric")
        void update() throws Exception {
            RepMetric original = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .conditionalMetricId(conditionalMetric.getId())
                    .value(3)
                    .build());

           String updateRequest = """
                                {
                                   "id"                  : %d,
                                   "repId"               : %d,
                                   "conditionalMetricId" : %d,
                                   "value"               : 6
                                }
                                """.formatted(original.getId(), rep.getId(), conditionalMetric.getId());

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
                                    "repId"               : %d,
                                    "conditionalMetricId" : %d,
                                    "dataSourceId"        : null,
                                    "value"               : 6
                                 }
                                """.formatted(original.getId(), rep.getId(), conditionalMetric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);
        }

        @Test
        @DisplayName("PUT /repMetrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"                  : 999999,
                                     "repId"               : %d,
                                     "conditionalMetricId" : %d
                                 }
                                 """.formatted(rep.getId(), conditionalMetric.getId());

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
        @DisplayName("DELETE /repMetrics/{id} -> deletes the RepMetric")
        void delete() throws Exception {
            RepMetric repMetric = repMetricRepository.save(RepMetric.builder()
                    .repId(rep.getId())
                    .conditionalMetricId(conditionalMetric.getId())
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
        @DisplayName("DELETE /repMetrics/{id} with unknown ID -> returns 404")
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
