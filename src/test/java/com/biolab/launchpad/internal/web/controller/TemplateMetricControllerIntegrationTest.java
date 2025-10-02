package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.TemplateMetricRepository;
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
@DisplayName("TemplateMetric Integration Tests")
class TemplateMetricControllerIntegrationTest {

    private static final String API = "/api/v1/template_metrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TemplateMetricRepository templateMetricRepository;

    @Autowired
    EntityFactory factory;

    AssessmentTemplate template;
    Metric         metric;
    DataSource     source;

    @BeforeEach
    void setUp() {
        template = factory.createAssessmentTemplate("template");
        metric   = factory.createMetric("metric");
        source   = factory.createDataSource("source");
    }

    @AfterEach
    void tearDown() {
        templateMetricRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /templateMetrics -> creates and returns the new TemplateMetric")
        void create() throws Exception {

            String request =
                            """ 
                                {
                                    "template_id": %d,
                                    "metric_id"  : %d,
                                    "source_id"  : %d
                                }
                            """.formatted(template.getId(), metric.getId(), source.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int templateMetricId = responseNode.get("id").asInt();
            assertThat(templateMetricId).isPositive();


            String expectedResponse =
                                    """
                                      {
                                        "id"         : %d,
                                        "template_id": %d,
                                        "metric_id"  : %d,
                                        "source_id"  : %d
                                      }
                                    """.formatted(templateMetricId, template.getId(), metric.getId(), source.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /templateMetrics with validation message -> returns 422")
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
                                "message"      : "Validation failed: metric_id: Template_metric metric_id cannot be null, and source_id: Template_metric source_id cannot be null, and template_id: Template_metric template_id cannot be null"
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
        @DisplayName("GET /templateMetrics -> returns all templateMetrics")
        void getAll() throws Exception {

            TemplateMetric templateMetric1 = templateMetricRepository.save(TemplateMetric.builder()
                    .template_id(template.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .build());

            TemplateMetric templateMetric2 = templateMetricRepository.save(TemplateMetric.builder()
                    .template_id(template.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
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
                        "id"         : %d,
                        "template_id": %d,
                        "metric_id"  : %d,
                        "source_id"  : %d
                    },
                    {
                        "id"         : %d,
                        "template_id": %d,
                        "metric_id"  : %d,
                        "source_id"  : %d
                    }
                ]
                """.formatted(templateMetric1.getId(), template.getId(), metric.getId(), source.getId(), templateMetric2.getId(),template.getId(), metric.getId(), source.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /templateMetrics/{id} -> returns templateMetric by ID")
        void getById() throws Exception {

            TemplateMetric templateMetric = templateMetricRepository.save(TemplateMetric.builder()
                    .template_id(template.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + templateMetric.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                     {
                                        "id"          : %d,
                                         "template_id": %d,
                                         "metric_id"  : %d,
                                         "source_id"  : %d
                                     }
                                    """.formatted(templateMetric.getId(), template.getId(), metric.getId(), source.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /templateMetrics/{id} with unknown ID -> returns 404")
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
                    "message": "Template_metric not found by id: 999999"
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
        @DisplayName("PUT /templateMetrics -> updates and returns the TemplateMetric")
        void update() throws Exception {
            TemplateMetric original = templateMetricRepository.save(TemplateMetric.builder()
                    .template_id(template.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .build());

            metric = factory.createMetric("updated");
            String updateRequest = """
                                {
                                    "id"          : %d,
                                    "template_id" : %d,
                                    "metric_id"   : %d,
                                    "source_id"   : %d
                                 }
                                """.formatted(original.getId(), template.getId(), metric.getId(), source.getId());

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
                                    "id"          : %d,
                                    "template_id" : %d,
                                    "metric_id"   : %d,
                                    "source_id"   : %d
                                 }
                                """.formatted(original.getId(), template.getId(), metric.getId(), source.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            TemplateMetric updated = templateMetricRepository.findById(original.getId()).orElseThrow();

        }

        @Test
        @DisplayName("PUT /templateMetrics with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"             : 999999,
                                     "template_id" : %d,
                                     "metric_id"   : %d,
                                     "source_id"   : %d
                                 }
                                 """.formatted(template.getId(), metric.getId(), source.getId());

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
                    "message": "TemplateMetricService. Could not update TemplateMetric by id: 999999"
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
        @DisplayName("DELETE /templateMetrics/{id} -> deletes the TemplateMetric")
        void delete() throws Exception {
            TemplateMetric templateMetric = templateMetricRepository.save(TemplateMetric.builder()
                    .template_id(template.getId())
                    .metric_id(metric.getId())
                    .source_id(source.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + templateMetric.getId())
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

            assertFalse(templateMetricRepository.existsById(templateMetric.getId()));
        }

        @Test
        @DisplayName("DELETE /templateMetrics/{id} with unknown ID -> returns 404")
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
                    "message": "TemplateMetricService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}