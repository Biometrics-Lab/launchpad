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

    private static final String API = "/api/v1/assessmentMetrics";

    @Autowired private MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired AssessmentMetricRepository assessmentMetricRepository;
    @Autowired EntityFactory factory;

    Assessment        assessment;
    ConditionalMetric conditionalMetric;
    DataSource        dataSource;

    @BeforeEach
    void setUp() {
        assessment        = factory.createAssessment();
        conditionalMetric = factory.createConditionalMetric();
        dataSource        = factory.createDataSource();
    }

    @AfterEach
    void tearDown() {
        assessmentMetricRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /assessmentMetrics -> creates and returns the new AssessmentMetric")
        void create() throws Exception {
            String request = """
                {
                    "assessmentId"        : %d,
                    "conditionalMetricId" : %d,
                    "dataSourceId"        : %d,
                    "minValue"            : 1,
                    "maxValue"            : 2,
                    "avgValue"            : 3
                }
            """.formatted(assessment.getId(), conditionalMetric.getId(), dataSource.getId());

            String jsonResponse = mvc.perform(post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);
            int id = responseNode.get("id").asInt();
            assertThat(id).isPositive();

            String expectedResponse = """
                {
                    "id"                  : %d,
                    "assessmentId"        : %d,
                    "conditionalMetricId" : %d,
                    "dataSourceId"        : %d,
                    "minValue"            : 1,
                    "maxValue"            : 2,
                    "avgValue"            : 3,
                    "description"         : null
                }
            """.formatted(id, assessment.getId(), conditionalMetric.getId(), dataSource.getId());

            assertEquals(objectMapper.readTree(expectedResponse), responseNode);
        }

        @Test
        @DisplayName("POST /assessmentMetrics with validation error -> returns 422")
        void createValidationError() throws Exception {
            String request = """
                { "description" : "" }
            """;

            String jsonResponse = mvc.perform(post(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(request)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "status"  : 422,
                    "message" : "Validation failed: assessmentId: AssesmentMetric assessmentId cannot be null, and conditionalMetricId: AssesmentMetric conditionalMetricId cannot be null, and dataSourceId: AssesmentMetric dataSourceId cannot be null"
                }
            """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }

    @Nested
    @DisplayName("Read")
    class ReadTests {

        @Test
        @DisplayName("GET /assessmentMetrics -> returns all assessmentMetrics")
        void getAll() throws Exception {
            AssessmentMetric am1 = assessmentMetricRepository.save(AssessmentMetric.builder()
                .assessmentId(assessment.getId()).conditionalMetricId(conditionalMetric.getId())
                .dataSourceId(dataSource.getId()).minValue(1).maxValue(2).avgValue(3).build());

            AssessmentMetric am2 = assessmentMetricRepository.save(AssessmentMetric.builder()
                .assessmentId(assessment.getId()).conditionalMetricId(conditionalMetric.getId())
                .dataSourceId(dataSource.getId()).minValue(5).maxValue(6).avgValue(7).build());

            String jsonResponse = mvc.perform(get(API).with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                [
                    {
                        "id": %d, "assessmentId": %d, "conditionalMetricId": %d, "dataSourceId": %d,
                        "minValue": 1, "maxValue": 2, "avgValue": 3, "description": null
                    },
                    {
                        "id": %d, "assessmentId": %d, "conditionalMetricId": %d, "dataSourceId": %d,
                        "minValue": 5, "maxValue": 6, "avgValue": 7, "description": null
                    }
                ]
            """.formatted(am1.getId(), assessment.getId(), conditionalMetric.getId(), dataSource.getId(),
                          am2.getId(), assessment.getId(), conditionalMetric.getId(), dataSource.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }

        @Test
        @DisplayName("GET /assessmentMetrics/{id} -> returns assessmentMetric by ID")
        void getById() throws Exception {
            AssessmentMetric am = assessmentMetricRepository.save(AssessmentMetric.builder()
                .assessmentId(assessment.getId()).conditionalMetricId(conditionalMetric.getId())
                .dataSourceId(dataSource.getId()).minValue(1).maxValue(2).avgValue(3).build());

            String jsonResponse = mvc.perform(get(API + "/" + am.getId()).with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id": %d, "assessmentId": %d, "conditionalMetricId": %d, "dataSourceId": %d,
                    "minValue": 1, "maxValue": 2, "avgValue": 3, "description": null
                }
            """.formatted(am.getId(), assessment.getId(), conditionalMetric.getId(), dataSource.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }

        @Test
        @DisplayName("GET /assessmentMetrics/{id} with unknown ID -> returns 404")
        void getByIdValidationError() throws Exception {
            String jsonResponse = mvc.perform(get(API + "/999999").with(httpBasic("biolab", "biolab")))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                { "status": 404, "message": "AssessmentMetric not found by id: 999999" }
            """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("PUT /assessmentMetrics -> updates and returns the AssessmentMetric")
        void update() throws Exception {
            AssessmentMetric original = assessmentMetricRepository.save(AssessmentMetric.builder()
                .assessmentId(assessment.getId()).conditionalMetricId(conditionalMetric.getId())
                .dataSourceId(dataSource.getId()).minValue(1).maxValue(2).avgValue(3).build());

            String updateRequest = """
                {
                    "id": %d, "assessmentId": %d, "conditionalMetricId": %d, "dataSourceId": %d,
                    "minValue": 4, "maxValue": 5, "avgValue": 6
                }
            """.formatted(original.getId(), assessment.getId(), conditionalMetric.getId(), dataSource.getId());

            String jsonResponse = mvc.perform(put(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateRequest)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id": %d, "assessmentId": %d, "conditionalMetricId": %d, "dataSourceId": %d,
                    "minValue": 4, "maxValue": 5, "avgValue": 6, "description": null
                }
            """.formatted(original.getId(), assessment.getId(), conditionalMetric.getId(), dataSource.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }

        @Test
        @DisplayName("PUT /assessmentMetrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {
            String updateRequest = """
                {
                    "id": 999999, "assessmentId": %d, "conditionalMetricId": %d, "dataSourceId": %d
                }
            """.formatted(assessment.getId(), conditionalMetric.getId(), dataSource.getId());

            String jsonResponse = mvc.perform(put(API)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateRequest)
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                { "status": 404, "message": "AssessmentMetricService. Could not update AssessmentMetric by id: 999999" }
            """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("DELETE /assessmentMetrics/{id} -> deletes the AssessmentMetric")
        void delete() throws Exception {
            AssessmentMetric am = assessmentMetricRepository.save(AssessmentMetric.builder()
                .assessmentId(assessment.getId()).conditionalMetricId(conditionalMetric.getId())
                .dataSourceId(dataSource.getId()).build());

            String jsonResponse = mvc.perform(MockMvcRequestBuilders.delete(API + "/" + am.getId())
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                { "status": 200, "message": "Success" }
            """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
            assertFalse(assessmentMetricRepository.existsById(am.getId()));
        }

        @Test
        @DisplayName("DELETE /assessmentMetrics/{id} with unknown ID -> returns 404")
        void deleteValidationError() throws Exception {
            String jsonResponse = mvc.perform(MockMvcRequestBuilders.delete(API + "/999999")
                    .with(httpBasic("biolab", "biolab")))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                { "status": 404, "message": "AssessmentMetricService. Could not delete id: 999999" }
            """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }
}
