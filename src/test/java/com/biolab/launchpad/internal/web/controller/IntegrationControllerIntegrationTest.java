package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.IntegrationRepository;
import com.biolab.launchpad.internal.repository.model.Integration;
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
@DisplayName("Integration Integration Tests")
class IntegrationControllerIntegrationTest {

    private static final String API = "/api/v1/integrations";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    IntegrationRepository integrationRepository;

    @Autowired
    EntityFactory factory;

    @AfterEach
    void tearDown() {
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /integrations -> creates and returns the new Integration")
        void create() throws Exception {

            String request = """
                    {
                        "name" : "Blast Motion"
                    }
                    """;

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
                        "id"   : %d,
                        "name" : "Blast Motion"
                    }
                    """.formatted(id);

            assertEquals(objectMapper.readTree(expectedResponse), responseNode);
        }

        @Test
        @DisplayName("POST /integrations with blank name -> returns 400")
        void createValidationError() throws Exception {
            String request = """
                    {
                        "name" : ""
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

            String expectedResponse = """
                    {
                        "status"  : 422,
                        "message" : "Validation failed: name: Name cannot be blank"
                    }
                    """;

            assertEquals(objectMapper.readTree(expectedResponse), responseNode);
        }
    }

    @Nested
    @DisplayName("Read")
    class ReadTests {

        @Test
        @DisplayName("GET /integrations -> returns all integrations")
        void getAll() throws Exception {

            Integration i1 = integrationRepository.save(Integration.builder().name("Blast Motion").build());
            Integration i2 = integrationRepository.save(Integration.builder().name("Rapsodo").build());

            String jsonResponse = mvc.perform(
                            get(API).with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    [
                        { "id" : %d, "name" : "Blast Motion" },
                        { "id" : %d, "name" : "Rapsodo" }
                    ]
                    """.formatted(i1.getId(), i2.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }

        @Test
        @DisplayName("GET /integrations/{id} -> returns integration by ID")
        void getById() throws Exception {

            Integration integration = integrationRepository.save(Integration.builder().name("Blast Motion").build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + integration.getId()).with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    { "id" : %d, "name" : "Blast Motion" }
                    """.formatted(integration.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }

        @Test
        @DisplayName("GET /integrations/{id} with unknown ID -> returns 404")
        void getByIdNotFound() throws Exception {
            String jsonResponse = mvc.perform(
                            get(API + "/999999").with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isNotFound())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "status" : 404,
                        "message": "Integration not found by id: 999999"
                    }
                    """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }

    @Nested
    @DisplayName("Update")
    class UpdateTests {

        @Test
        @DisplayName("PUT /integrations -> updates and returns the Integration")
        void update() throws Exception {
            Integration original = integrationRepository.save(Integration.builder().name("Blast Motion").build());

            String updateRequest = """
                    {
                        "id"   : %d,
                        "name" : "Blast Motion v2"
                    }
                    """.formatted(original.getId());

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
                        "id"   : %d,
                        "name" : "Blast Motion v2"
                    }
                    """.formatted(original.getId());

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));

            Integration updated = integrationRepository.findById(original.getId()).orElseThrow();
            assertEquals("Blast Motion v2", updated.getName());
        }

        @Test
        @DisplayName("PUT /integrations with invalid id -> returns 404")
        void updateNotFound() throws Exception {
            String updateRequest = """
                    {
                        "id"   : 999999,
                        "name" : "Blast Motion"
                    }
                    """;

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
                        "message": "IntegrationService. Could not update Integration by id: 999999"
                    }
                    """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }

    @Nested
    @DisplayName("Delete")
    class DeleteTests {

        @Test
        @DisplayName("DELETE /integrations/{id} -> deletes the Integration")
        void delete() throws Exception {
            Integration integration = integrationRepository.save(Integration.builder().name("Blast Motion").build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + integration.getId())
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
            assertFalse(integrationRepository.existsById(integration.getId()));
        }

        @Test
        @DisplayName("DELETE /integrations/{id} with unknown ID -> returns 404")
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
                        "message": "IntegrationService. Could not delete id: 999999"
                    }
                    """;

            assertEquals(objectMapper.readTree(expectedResponse), objectMapper.readTree(jsonResponse));
        }
    }
}
