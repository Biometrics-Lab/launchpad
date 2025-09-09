package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.MeasurementRepository;
import com.biolab.launchpad.internal.repository.MetricRepository;
import com.biolab.launchpad.internal.repository.model.Measurement;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MetricController Integration Tests")
class MetricControllerIntegrationTest {

    private static final String API = "/api/v1/metrics";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MeasurementRepository measurementRepository;
    @Autowired
    MetricRepository metricRepository;

    Measurement mph;

    @BeforeEach
    void setUp() {
        mph = measurementRepository.save(
                Measurement.builder()
                        .name("Mph")
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        metricRepository.deleteAll();
        measurementRepository.deleteAll();
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
        @DisplayName("POST /metrics with validation message -> returns 400")
        void createValidationError() throws Exception {

            String request =
                    """ 
                                {
                                    "negate"       : true
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
                                "message"        : "Validation failed: measurementId: Measurement ID cannot be null, and name: Name cannot be blank"
                            }
                            """;

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }
    }

    //Please, finish the tests for Read, Update, Delete, and List operations
}