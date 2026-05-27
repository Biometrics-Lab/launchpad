package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.DataSourceRepository;
import com.biolab.launchpad.internal.repository.model.DataSource;
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
@DisplayName("DataSource Integration Tests")
class DataSourceControllerIntegrationTest {

    private static final String API = "/api/v1/dataSources";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DataSourceRepository dataSourceRepository;

    @Autowired
    EntityFactory factory;

    Metric metric;

    @BeforeEach
    void setUp() {
        metric = factory.createMetric("ExitVelocity");
    }

    @AfterEach
    void tearDown() {
        dataSourceRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /dataSources -> creates and returns the new DataSource")
        void create() throws Exception {

            String request = """
                    {
                        "metricId"      : %d,
                        "name"          : "Exit Velocity",
                        "type"          : "JSON_CONFIG",
                        "content"       : "{\\"path\\": \\"$.exitVelocity\\"}"
                    }
                    """.formatted(metric.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int dataSourceId = responseNode.get("id").asInt();
            assertThat(dataSourceId).isPositive();

            String expectedResponse = """
                    {
                        "id"       : %d,
                        "metricId" : %d,
                        "name"     : "Exit Velocity",
                        "type"     : "JSON_CONFIG",
                        "content"  : "{\\"path\\": \\"$.exitVelocity\\"}"
                    }
                    """.formatted(dataSourceId, metric.getId());

            assertEquals(objectMapper.readTree(expectedResponse), responseNode);
        }

        @Test
        @DisplayName("POST /dataSources with validation error -> returns 400")
        void createValidationError() throws Exception {
            String request = """
                    {
                        "content" : "some content"
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

            assertThat(responseNode.get("status").asInt()).isEqualTo(422);
        }
    }

    @Nested
    @DisplayName("Read")
    class ReadTests {

        @Test
        @DisplayName("GET /dataSources -> returns all dataSources")
        void getAll() throws Exception {

            DataSource ds1 = dataSourceRepository.save(DataSource.builder()
                    .metricId(metric.getId())
                    .type("JSON_CONFIG")
                    .build());

            DataSource ds2 = dataSourceRepository.save(DataSource.builder()
                    .metricId(metric.getId())
                    .type("SCRIPT")
                    .content("return payload.speed;")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API).with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    [
                        {
                            "id"       : %d,
                            "metricId" : %d,
                            "name"     : null,
                            "type"     : "JSON_CONFIG",
                            "content"  : null
                        },
                        {
                            "id"       : %d,
                            "metricId" : %d,
                            "name"     : null,
                            "type"     : "SCRIPT",
                            "content"  : "return payload.speed;"
                        }
                    ]
                    """.formatted(ds1.getId(), metric.getId(),
                                  ds2.getId(), metric.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }

        @Test
        @DisplayName("GET /dataSources/{id} -> returns dataSource by ID")
        void getById() throws Exception {

            DataSource ds = dataSourceRepository.save(DataSource.builder()
                    .metricId(metric.getId())
                    .type("MAPPING")
                    .content("exit_velocity")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + ds.getId()).with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "id"       : %d,
                        "metricId" : %d,
                        "name"     : null,
                        "type"     : "MAPPING",
                        "content"  : "exit_velocity"
                    }
                    """.formatted(ds.getId(), metric.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }

        @Test
        @DisplayName("GET /dataSources/{id} with unknown ID -> returns 404")
        void getByIdNotFound() throws Exception {
            String jsonResponse = mvc.perform(
                            get(API + "/999999").with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "status" : 404,
                        "message": "DataSource not found by id: 999999"
                    }
                    """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("PUT /dataSources -> updates and returns the DataSource")
        void update() throws Exception {
            DataSource original = dataSourceRepository.save(DataSource.builder()
                    .metricId(metric.getId())
                    .type("JSON_CONFIG")
                    .build());

            String updateRequest = """
                    {
                        "id"       : %d,
                        "metricId" : %d,
                        "type"     : "SCRIPT",
                        "content"  : "return payload.v;"
                    }
                    """.formatted(original.getId(), metric.getId());

            String jsonResponse = mvc.perform(
                            put(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(updateRequest)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "id"       : %d,
                        "metricId" : %d,
                        "name"     : null,
                        "type"     : "SCRIPT",
                        "content"  : "return payload.v;"
                    }
                    """.formatted(original.getId(), metric.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));

            DataSource updated = dataSourceRepository.findById(original.getId()).orElseThrow();
            assertEquals("SCRIPT", updated.getType());
        }

        @Test
        @DisplayName("PUT /dataSources with invalid id -> returns 404")
        void updateNotFound() throws Exception {
            String updateRequest = """
                    {
                        "id"       : 999999,
                        "metricId" : %d,
                        "type"     : "JSON_CONFIG"
                    }
                    """.formatted(metric.getId());

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
                        "message": "DataSourceService. Could not update DataSource by id: 999999"
                    }
                    """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("DELETE /dataSources/{id} -> deletes the DataSource")
        void delete() throws Exception {
            DataSource ds = dataSourceRepository.save(DataSource.builder()
                    .metricId(metric.getId())
                    .type("JSON_CONFIG")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + ds.getId())
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

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
            assertFalse(dataSourceRepository.existsById(ds.getId()));
        }

        @Test
        @DisplayName("DELETE /dataSources/{id} with unknown ID -> returns 404")
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
                        "message": "DataSourceService. Could not delete id: 999999"
                    }
                    """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }
}
