package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.RepMetricSourceRepository;
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
@DisplayName("RepMetricSource Integration Tests")
class RepMetricSourceControllerIntegrationTest {

    private static final String API = "/api/v1/repMetricSources";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RepMetricSourceRepository repMetricSourceRepository;

    @Autowired
    EntityFactory factory;

    RepMetric  repMetric;
    DataSource dataSource;

    @BeforeEach
    void setUp() {
        repMetric   = factory.createRepMetric("rrMetric");
        dataSource  = factory.createDataSource("dSource");
    }

    @AfterEach
    void tearDown() {
        repMetricSourceRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /repMetricSources -> creates and returns the new RepMetricSource")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "repMetricId"    : %d,
                                    "dataSourceId"   : %d,
                                    "description"    : "desc"
                                }
                            """.formatted(repMetric.getId(), dataSource.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int repMetricSourceId = responseNode.get("id").asInt();
            assertThat(repMetricSourceId).isPositive();


            String expectedResponse =
                    """
                            {
                                        "id"             : %d,
                                        "repMetricId"    : %d,
                                        "dataSourceId"   : %d,
                                        "description"    : "desc"
                                    }
                            """.formatted(repMetricSourceId, repMetric.getId(), dataSource.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /repMetricSources with validation message -> returns 422")
        void createValidationError() throws Exception {
            String request =
                            """
                                {
                                    "description" : "desc"
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
                                "message"      : "Validation failed: dataSourceId: RepMetricSource dataSourceId cannot be null, and repMetricId: RepMetricSource repMetricId cannot be null"
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
        @DisplayName("GET /repMetricSources -> returns all repMetricSources")
        void getAll() throws Exception {

            RepMetricSource repMetricSource1 = repMetricSourceRepository.save(RepMetricSource.builder()
                    .repMetricId(repMetric.getId())
                    .dataSourceId(dataSource.getId())
                    .description("desc")
                    .build());

            RepMetricSource repMetricSource2 = repMetricSourceRepository.save(RepMetricSource.builder()
                    .repMetricId(repMetric.getId())
                    .dataSourceId(dataSource.getId())
                    .description("desc")
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
                        "id"             : %d,
                        "repMetricId"    : %d,
                        "dataSourceId"   : %d,
                        "description"    : "desc"
                    },
                    {
                        "id"             : %d,
                        "repMetricId"    : %d,
                        "dataSourceId"   : %d,
                        "description"    : "desc"
                    }
                ]
                """.formatted(repMetricSource1.getId(), repMetric.getId(), dataSource.getId(), repMetricSource2.getId(), repMetric.getId(), dataSource.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /repMetricSources/{id} -> returns repMetricSource by ID")
        void getById() throws Exception {

            RepMetricSource repMetricSource = repMetricSourceRepository.save(RepMetricSource.builder()
                    .repMetricId(repMetric.getId())
                    .dataSourceId(dataSource.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + repMetricSource.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"             : %d,
                    "repMetricId"    : %d,
                    "dataSourceId"   : %d,
                    "description"    : "desc"
                }
                """.formatted(repMetricSource.getId(), repMetric.getId() ,dataSource.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /repMetricSources/{id} with unknown ID -> returns 404")
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
                    "message": "RepMetricSource not found by id: 999999"
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
        @DisplayName("PUT /repMetricSources -> updates and returns the RepMetricSource")
        void update() throws Exception {
            RepMetricSource original = repMetricSourceRepository.save(RepMetricSource.builder()
                    .repMetricId(repMetric.getId())
                    .dataSourceId(dataSource.getId())
                    .description("desc")
                    .build());

            String updateRequest = """
                {
                    "id"             : %d,
                    "repMetricId"    : %d,
                    "dataSourceId"   : %d,
                    "description"    : "descUPD"
                }
                """.formatted(original.getId(), repMetric.getId(), dataSource.getId());

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
                    "id"             : %d,
                    "repMetricId"    : %d,
                    "dataSourceId"   : %d,
                    "description"    : "descUPD"
                }
                """.formatted(original.getId(), repMetric.getId(), dataSource.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            RepMetricSource updated = repMetricSourceRepository.findById(original.getId()).orElseThrow();
            assertEquals("descUPD", updated.getDescription());
        }

        @Test
        @DisplayName("PUT /repMetricSources with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"             : 999999,
                    "repMetricId"    : %d,
                    "dataSourceId"   : %d,
                    "description"    : "desc"
                }
                """.formatted(repMetric.getId(), dataSource.getId());

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
                    "message": "RepMetricSourceService. Could not update RepMetricSource by id: 999999"
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
        @DisplayName("DELETE /repMetricSources/{id} -> deletes the RepMetricSource")
        void delete() throws Exception {
            RepMetricSource repMetricSource = repMetricSourceRepository.save(RepMetricSource.builder()
                    .repMetricId(repMetric.getId())
                    .dataSourceId(dataSource.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + repMetricSource.getId())
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

            assertFalse(repMetricSourceRepository.existsById(repMetricSource.getId()));
        }

        @Test
        @DisplayName("DELETE /repMetricSources/{id} with unknown ID -> returns 404")
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
                    "message": "RepMetricSourceService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}