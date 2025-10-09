package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.MetricRepository;
import com.biolab.launchpad.internal.repository.model.Measurement;
import com.biolab.launchpad.internal.repository.model.Metric;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MetricController Integration Tests")
class MetricControllerIntegrationTest{

    private static final String API = "/api/v1/metrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MetricRepository metricRepository;

    @Autowired
    EntityFactory factory;

    Measurement mph;

    @BeforeEach
    void setUp() {
        mph = factory.createMeasurement("Mph");
    }

    @AfterEach
    void tearDown() {
        metricRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /metrics -> creates and returns the new Metric")
        void create() throws Exception {

            String request =
                            """ 
                                {
                                    "name"         : "avg_exit_velocity",
                                    "measurementId": %d,
                                    "negate"       : false
                                }
                            """.formatted(mph.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int metricId = responseNode.get("id").asInt();
            assertThat(metricId).isPositive();


            String expectedResponse =
                                    """ 
                                    {
                                        "id"           : %d,
                                        "name"         : "avg_exit_velocity",
                                        "measurementId": %d,
                                        "negate"       : false
                                    }
                                    """.formatted(metricId, mph.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode); // deep comparison without order
        }

        @Test
        @DisplayName("POST /metrics with validation message -> returns 422")
        void createValidationError() throws Exception {

            String request =
                            """
                                {
                                    "negate" : true
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
                                "message" : "Validation failed: measurementId: MeasurementId cannot be null, and name: Name cannot be blank"
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
        @DisplayName("GET /metrics -> returns all metrics")
        void getAll() throws Exception {

            Metric metric1 = metricRepository.save(Metric.builder()
                    .name("avg_exit_velocity")
                    .measurementId(mph.getId())
                    .negate(false)
                    .build());

            Metric metric2 = metricRepository.save(Metric.builder()
                    .name("max_entry_velocity")
                    .measurementId(mph.getId())
                    .negate(true)
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
                            "name"         : "avg_exit_velocity",
                            "measurementId": %d,
                            "negate"       : false
                        },
                        {
                            "id"           : %d,
                            "name"         : "max_entry_velocity",
                            "measurementId": %d,
                            "negate"       : true
                        }
                    ]
                    """.formatted(metric1.getId(), mph.getId(), metric2.getId(), mph.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /metrics/{id} -> returns metric by ID")
        void getById() throws Exception {

            Metric metric = metricRepository.save(Metric.builder()
                    .name("avg_exit_velocity")
                    .measurementId(mph.getId())
                    .negate(false)
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + metric.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "id"           : %d,
                        "name"         : "avg_exit_velocity",
                        "measurementId": %d,
                        "negate"       : false
                    }
                    """.formatted(metric.getId(), mph.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /metrics/{id} with unknown ID -> returns 404")
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
                        "message": "Metric not found by id: 999999"
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
        @DisplayName("PUT /metrics -> updates and returns the Metric")
        void update() throws Exception {
            Metric original = metricRepository.save(Metric.builder()
                    .name("avg_exit_velocity")
                    .measurementId(mph.getId())
                    .negate(true)
                    .build());

            String updateRequest = """
                    {
                        "id"            : %d,
                        "name"          : "updated_velocity",
                        "measurementId" : %d,
                        "negate"        : false
                    }
                    """.formatted(original.getId(), mph.getId());

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
                        "name"         : "updated_velocity",
                        "measurementId": %d,
                        "negate"       : false
                    }
                    """.formatted(original.getId(), mph.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            Metric updated = metricRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated_velocity", updated.getName());
            assertFalse(updated.isNegate());

        }

        @Test
        @DisplayName("PUT /metrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                    {
                        "id"            : 999999,
                        "name"          : "updated_velocity",
                        "measurementId": %d
                    }
                    """.formatted(mph.getId());

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
                        "message": "MetricService. Could not update Metric by id: 999999"
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
        @DisplayName("DELETE /metrics/{id} -> deletes the Metric")
        void delete() throws Exception {
            Metric metric = metricRepository.save(Metric.builder()
                    .name("avg_exit_velocity")
                    .measurementId(mph.getId())
                    .negate(false)
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + metric.getId())
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

            assertFalse(metricRepository.existsById(metric.getId()));
        }

        @Test
        @DisplayName("DELETE /metrics/{id} with unknown ID -> returns 404")
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
                        "message": "MetricService. Could not delete id: 999999"
                    }
                    """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }
}