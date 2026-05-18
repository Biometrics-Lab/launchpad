package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.ConditionalMetricRepository;
import com.biolab.launchpad.internal.repository.model.Condition;
import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ConditionalMetric Integration Tests")
class ConditionalMetricControllerIntegrationTest {

    private static final String API = "/api/v1/conditionalMetrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ConditionalMetricRepository conditionalMetricRepository;

    @Autowired
    EntityFactory factory;

    Condition condition;
    Metric    metric;

    @BeforeEach
    void setUp() {
        condition = factory.createCondition("AutoCondition");
        metric    = factory.createMetric("AutoMetric");
    }

    @AfterEach
    void tearDown() {
        conditionalMetricRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /conditionalMetrics -> creates and returns the new ConditionalMetric")
        void create() throws Exception {

            String request = """
                    {
                        "name"        : "Exit velocity from T",
                        "conditionId" : %d,
                        "metricId"    : %d
                    }
                    """.formatted(condition.getId(), metric.getId());

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

            String expectedResponse = """
                    {
                        "id"          : %d,
                        "name"        : "Exit velocity from T",
                        "conditionId" : %d,
                        "metricId"    : %d
                    }
                    """.formatted(id, condition.getId(), metric.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /conditionalMetrics with validation error -> returns 422")
        void createValidationError() throws Exception {
            String request = """
                    {}
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

            String expectedResponse = """
                    {
                        "status"  : 422,
                        "message" : "Validation failed: conditionId: ConditionalMetric conditionId cannot be null, and metricId: ConditionalMetric metricId cannot be null, and name: Name cannot be blank"
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
        @DisplayName("GET /conditionalMetrics -> returns all conditionalMetrics")
        void getAll() throws Exception {

            ConditionalMetric cm1 = conditionalMetricRepository.save(ConditionalMetric.builder()
                    .name("Exit velocity from T")
                    .conditionId(condition.getId())
                    .metricId(metric.getId())
                    .build());

            ConditionalMetric cm2 = conditionalMetricRepository.save(ConditionalMetric.builder()
                    .name("Spin rate pitching machine")
                    .conditionId(condition.getId())
                    .metricId(metric.getId())
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
                            "id"          : %d,
                            "name"        : "Exit velocity from T",
                            "conditionId" : %d,
                            "metricId"    : %d
                        },
                        {
                            "id"          : %d,
                            "name"        : "Spin rate pitching machine",
                            "conditionId" : %d,
                            "metricId"    : %d
                        }
                    ]
                    """.formatted(cm1.getId(), condition.getId(), metric.getId(),
                                  cm2.getId(), condition.getId(), metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /conditionalMetrics/{id} -> returns conditionalMetric by ID")
        void getById() throws Exception {

            ConditionalMetric cm = conditionalMetricRepository.save(ConditionalMetric.builder()
                    .name("Exit velocity from T")
                    .conditionId(condition.getId())
                    .metricId(metric.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + cm.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "id"          : %d,
                        "name"        : "Exit velocity from T",
                        "conditionId" : %d,
                        "metricId"    : %d
                    }
                    """.formatted(cm.getId(), condition.getId(), metric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /conditionalMetrics/{id} with unknown ID -> returns 404")
        void getByIdNotFound() throws Exception {
            String jsonResponse = mvc.perform(
                            get(API + "/999999")
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "status" : 404,
                        "message": "ConditionalMetric not found by id: 999999"
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
        @DisplayName("PUT /conditionalMetrics -> updates and returns the ConditionalMetric")
        void update() throws Exception {
            ConditionalMetric original = conditionalMetricRepository.save(ConditionalMetric.builder()
                    .name("Exit velocity from T")
                    .conditionId(condition.getId())
                    .metricId(metric.getId())
                    .build());

            Metric updatedMetric = factory.createMetric("UpdatedMetric");

            String updateRequest = """
                    {
                        "id"          : %d,
                        "name"        : "Exit velocity from T",
                        "conditionId" : %d,
                        "metricId"    : %d
                    }
                    """.formatted(original.getId(), condition.getId(), updatedMetric.getId());

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
                        "name"        : "Exit velocity from T",
                        "conditionId" : %d,
                        "metricId"    : %d
                    }
                    """.formatted(original.getId(), condition.getId(), updatedMetric.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            ConditionalMetric updated = conditionalMetricRepository.findById(original.getId()).orElseThrow();
            assertEquals(updated.getMetricId(), updatedMetric.getId());
        }

        @Test
        @DisplayName("PUT /conditionalMetrics with invalid id -> returns 404")
        void updateNotFound() throws Exception {

            String updateRequest = """
                    {
                        "id"          : 999999,
                        "name"        : "Exit velocity from T",
                        "conditionId" : %d,
                        "metricId"    : %d
                    }
                    """.formatted(condition.getId(), metric.getId());

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
                        "message": "ConditionalMetricService. Could not update ConditionalMetric by id: 999999"
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
        @DisplayName("DELETE /conditionalMetrics/{id} -> deletes the ConditionalMetric")
        void delete() throws Exception {
            ConditionalMetric cm = conditionalMetricRepository.save(ConditionalMetric.builder()
                    .name("Exit velocity from T")
                    .conditionId(condition.getId())
                    .metricId(metric.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + cm.getId())
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

            assertFalse(conditionalMetricRepository.existsById(cm.getId()));
        }

        @Test
        @DisplayName("DELETE /conditionalMetrics/{id} with unknown ID -> returns 404")
        void deleteNotFound() throws Exception {
            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/999999")
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "status" : 404,
                        "message": "ConditionalMetricService. Could not delete id: 999999"
                    }
                    """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }
}
