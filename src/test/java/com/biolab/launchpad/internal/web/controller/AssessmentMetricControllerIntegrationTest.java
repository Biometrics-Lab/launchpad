package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
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
@DisplayName("Assessment metric Integration Tests")
class AssessmentMetricControllerIntegrationTest {

    private static final String API = "/api/v1/assessment_metrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AssessmentMetricRepository assessmentMetricRepository;

    @Autowired
    EntityFactory factory;

    Assessment assessment;
    Metric     metric;
    DataSource source;

    @BeforeEach
    void setUp() {
        assessment = factory.createAssessment("five");
        metric     = factory.createMetric("height");
        source     = factory.createDataSource("Dsource");
    }

    @AfterEach
    void tearDown() {
        assessmentMetricRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /assessment_metrics -> creates and returns the new Assessment")
        void create() throws Exception {

            String request =
                            """ 
                                {
                                    "assessment_id": %d,
                                    "metric_id"    : %d,
                                    "source_id"    : %d,
                                    "min_value"    : 1,
                                    "max_value"    : 2,
                                    "avg_value"    : 3,
                                    "last_value"   : 4
                                }
                            """.formatted(assessment.getId(), metric.getId(), source.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int assessmentId = responseNode.get("id").asInt();
            assertThat(assessmentId).isPositive();


            String expectedResponse =
                                    """
                                      {
                                        "id"           : %d,
                                        "assessment_id": %d,
                                        "metric_id"    : %d,
                                        "source_id"    : %d,
                                        "min_value"    : 1,
                                        "max_value"    : 2,
                                        "avg_value"    : 3,
                                        "last_value"   : 4
                                      }
                                    """.formatted(assessmentId, assessment.getId(), metric.getId(), source.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /assessment_metrics with validation message -> returns 422")
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
                                "message"      : "Validation failed: assessment_id: Assesment_metric assessment_id cannot be null, and metric_id: Assesment_metric metric_id cannot be null, and source_id: Assesment_metric source_id cannot be null"
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
        @DisplayName("GET /assessment_metrics -> returns all assessment_metrics")
        void getAll() throws Exception {

            AssessmentMetric assessmentMetric1 = assessmentMetricRepository.save(AssessmentMetric.builder()
                    .assessment_id(assessment.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .min_value(1)
                    .max_value(2)
                    .avg_value(3)
                    .last_value(4)
                    .build());

            AssessmentMetric assessmentMetric2 = assessmentMetricRepository.save(AssessmentMetric.builder()
                    .assessment_id(assessment.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .min_value(5)
                    .max_value(6)
                    .avg_value(7)
                    .last_value(8)
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
                        "assessment_id": %d,
                        "metric_id"    : %d,
                        "source_id"    : %d,
                        "min_value"    : 1,
                        "max_value"    : 2,
                        "avg_value"    : 3,
                        "last_value"   : 4
                    },
                    {
                        "id"           : %d,
                        "assessment_id": %d,
                        "metric_id"    : %d,
                        "source_id"    : %d,
                        "min_value"    : 5,
                        "max_value"    : 6,
                        "avg_value"    : 7,
                        "last_value"   : 8
                    }
                ]
                """.formatted(assessmentMetric1.getId(), assessment.getId(), metric.getId(), source.getId(), assessmentMetric2.getId(),assessment.getId(), metric.getId(), source.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessment_metrics/{id} -> returns assessment by ID")
        void getById() throws Exception {

            AssessmentMetric assessmentMetric = assessmentMetricRepository.save(AssessmentMetric.builder()
                    .assessment_id(assessment.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .min_value(1)
                    .max_value(2)
                    .avg_value(3)
                    .last_value(4)
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + assessmentMetric.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                     {
                                        "id"            : %d,
                                         "assessment_id": %d,
                                         "metric_id"    : %d,
                                         "source_id"    : %d,
                                         "min_value"    : 1,
                                         "max_value"    : 2,
                                         "avg_value"    : 3,
                                         "last_value"   : 4
                                     }
                                    """.formatted(assessmentMetric.getId(), assessment.getId(), metric.getId(), source.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessment_metrics/{id} with unknown ID -> returns 404")
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
                    "message": "Assessment_metric not found by id: 999999"
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
        @DisplayName("PUT /assessment_metrics -> updates and returns the Assessment")
        void update() throws Exception {
            AssessmentMetric original = assessmentMetricRepository.save(AssessmentMetric.builder()
                    .assessment_id(assessment.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .min_value(1)
                    .max_value(2)
                    .avg_value(3)
                    .last_value(4)
                    .build());

           String updateRequest = """
                                {
                                        "id"            : %d,
                                         "assessment_id": %d,
                                         "metric_id"    : %d,
                                         "source_id"    : %d,
                                         "min_value"    : 4,
                                         "max_value"    : 5,
                                         "avg_value"    : 6,
                                         "last_value"   : 7
                                     }
                                """.formatted(original.getId(), assessment.getId(), metric.getId(), source.getId());

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
                                    "assessment_id": %d,
                                    "metric_id"    : %d,
                                    "source_id"    : %d,
                                    "min_value"    : 4,
                                    "max_value"    : 5,
                                    "avg_value"    : 6,
                                    "last_value"   : 7
                                 }
                                """.formatted(original.getId(), assessment.getId(), metric.getId(), source.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);
        }

        @Test
        @DisplayName("PUT /assessment_metrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"           : 999999,
                                     "assessment_id": %d,
                                     "metric_id"    : %d,
                                     "source_id"    : %d
                                 }
                                 """.formatted(assessment.getId(), metric.getId(), source.getId());

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
                    "message": "AssessmentMetricService. Could not update AssessmentMetric by id: 999999"
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
        @DisplayName("DELETE /assessment_metrics/{id} -> deletes the Assessment")
        void delete() throws Exception {
            AssessmentMetric assessmentMetric = assessmentMetricRepository.save(AssessmentMetric.builder()
                    .assessment_id(assessment.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + assessmentMetric.getId())
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

            assertFalse(assessmentMetricRepository.existsById(assessmentMetric.getId()));
        }

        @Test
        @DisplayName("DELETE /assessment_metrics/{id} with unknown ID -> returns 404")
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
                    "message": "AssessmentMetricService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}