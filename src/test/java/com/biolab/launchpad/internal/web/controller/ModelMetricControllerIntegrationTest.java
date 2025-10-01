package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.ModelMetricRepository;
import com.biolab.launchpad.internal.repository.model.Metric;
import com.biolab.launchpad.internal.repository.model.Model;
import com.biolab.launchpad.internal.repository.model.ModelMetric;
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
@DisplayName("ModelMetric Integration Tests")
class ModelMetricControllerIntegrationTest {

    private static final String API = "/api/v1/model_metrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelMetricRepository modelMetricRepository;

    @Autowired
    EntityFactory factory;

    Model model;
    Metric metric;

    @BeforeEach
    void setUp() {
        model  = factory.createModel("mod");
        metric = factory.createMetric("metr");
    }

    @AfterEach
    void tearDown() {
        modelMetricRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /modelMetrics -> creates and returns the new ModelMetric")
        void create() throws Exception {

            String request =
                            """ 
                                {
                                    "model_id"    : %d,
                                    "metric_id"   : %d,
                                    "value"       : 1
                                }
                            """.formatted(model.getId(), metric.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int modelMetricId = responseNode.get("id").asInt();
            assertThat(modelMetricId).isPositive();


            String expectedResponse =
                    """ 
                            {
                                        "id"          : %d,
                                        "model_id"    : %d,
                                        "metric_id"   : %d,
                                        "value"       : 1
                                    }
                            """.formatted(modelMetricId, model.getId(), metric.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /modelMetrics with validation message -> returns 422")
        void createValidationError() throws Exception {
            String request =
                            """
                                {
                                    "value" : 1
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
                                "message"      : "Validation failed: metric_id: Model_metric metric_id cannot be null, and model_id: Model_metric model_id cannot be null"
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
        @DisplayName("GET /modelMetrics -> returns all modelMetrics")
        void getAll() throws Exception {

            ModelMetric modelMetric1 = modelMetricRepository.save(ModelMetric.builder()
                    .model_id(model.getId())
                    .metric_id(metric.getId())
                    .value(1)
                    .build());

            ModelMetric modelMetric2 = modelMetricRepository.save(ModelMetric.builder()
                    .model_id(model.getId())
                    .metric_id(metric.getId())
                    .value(1)
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
                        "id"            : %d,
                        "model_id"      : %d,
                        "metric_id"     : %d,
                        "value"         : 1
                        
                    },
                    {
                        "id"             : %d,
                        "model_id"       : %d,
                        "metric_id"      : %d,
                        "value"          : 1
                    }
                ]
                """.formatted(modelMetric1.getId(), model.getId(), metric.getId(), modelMetric2.getId(), model.getId(), metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /modelMetrics/{id} -> returns modelMetric by ID")
        void getById() throws Exception {

            ModelMetric modelMetric = modelMetricRepository.save(ModelMetric.builder()
                    .model_id(model.getId())
                    .metric_id(metric.getId())
                    .value(1)
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + modelMetric.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"            : %d,
                    "model_id"      : %d,
                    "metric_id"     : %d,
                    "value"         : 1
                }
                """.formatted(modelMetric.getId(), model.getId() ,metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /modelMetrics/{id} with unknown ID -> returns 404")
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
                    "message": "Model_metric not found by id: 999999"
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
        @DisplayName("PUT /modelMetrics -> updates and returns the ModelMetric")
        void update() throws Exception {
            ModelMetric original = modelMetricRepository.save(ModelMetric.builder()
                    .model_id(model.getId())
                    .metric_id(metric.getId())
                    .build());

            String updateRequest = """
                {
                    "id"          : %d,
                    "model_id"    : %d,
                    "metric_id"   : %d,
                    "value"       : 1
                }
                """.formatted(original.getId(), model.getId(), metric.getId());

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
                    "model_id"     : %d,
                    "metric_id"    : %d,
                    "value"        : 1
                }
                """.formatted(original.getId(), model.getId(), metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            ModelMetric updated = modelMetricRepository.findById(original.getId()).orElseThrow();

        }

        @Test
        @DisplayName("PUT /modelMetrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"            : 999999,
                    "model_id"      : %d,
                    "metric_id"     : %d,
                    "value"         : 1
                }
                """.formatted(model.getId(), metric.getId());

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
                    "message": "ModelMetricService. Could not update ModelMetric by id: 999999"
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
        @DisplayName("DELETE /modelMetrics/{id} -> deletes the ModelMetric")
        void delete() throws Exception {
            ModelMetric modelMetric = modelMetricRepository.save(ModelMetric.builder()
                    .model_id(model.getId())
                    .metric_id(metric.getId())
                    .value(1)
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + modelMetric.getId())
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

            assertFalse(modelMetricRepository.existsById(modelMetric.getId()));
        }

        @Test
        @DisplayName("DELETE /modelMetrics/{id} with unknown ID -> returns 404")
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
                    "message": "ModelMetricService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}