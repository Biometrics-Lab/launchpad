package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.ConditionRepository;
import com.biolab.launchpad.internal.repository.model.Condition;
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
@DisplayName("Condition Integration Tests")
class ConditionControllerIntegrationTest {

    private static final String API = "/api/v1/conditions";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ConditionRepository conditionRepository;

    @Autowired
    EntityFactory factory;

    @AfterEach
    void tearDown() {
        conditionRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /conditions -> creates and returns the new Condition")
        void create() throws Exception {

            String request = """
                    {
                        "name" : "Hitting from T"
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
                        "id"         : %d,
                        "name"       : "Hitting from T",
                        "sport"      : null,
                        "templateId" : null
                    }
                    """.formatted(id);

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /conditions with validation error -> returns 422")
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
                        "message" : "Validation failed: name: Name cannot be blank"
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
        @DisplayName("GET /conditions -> returns all conditions")
        void getAll() throws Exception {

            Condition condition1 = conditionRepository.save(Condition.builder()
                    .name("Hitting from T")
                    .build());

            Condition condition2 = conditionRepository.save(Condition.builder()
                    .name("Pitching machine 60ft")
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
                            "name"       : "Hitting from T",
                            "sport"      : null,
                            "templateId" : null
                        },
                        {
                            "id"         : %d,
                            "name"       : "Pitching machine 60ft",
                            "sport"      : null,
                            "templateId" : null
                        }
                    ]
                    """.formatted(condition1.getId(), condition2.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /conditions/{id} -> returns condition by ID")
        void getById() throws Exception {

            Condition condition = conditionRepository.save(Condition.builder()
                    .name("Hitting from T")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + condition.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                    {
                        "id"         : %d,
                        "name"       : "Hitting from T",
                        "sport"      : null,
                        "templateId" : null
                    }
                    """.formatted(condition.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /conditions/{id} with unknown ID -> returns 404")
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
                        "message": "Condition not found by id: 999999"
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
        @DisplayName("PUT /conditions -> updates and returns the Condition")
        void update() throws Exception {
            Condition original = conditionRepository.save(Condition.builder()
                    .name("Hitting from T")
                    .build());

            String updateRequest = """
                    {
                        "id"   : %d,
                        "name" : "Live at-bat"
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

            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            String expectedResponse = """
                    {
                        "id"         : %d,
                        "name"       : "Live at-bat",
                        "sport"      : null,
                        "templateId" : null
                    }
                    """.formatted(original.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            Condition updated = conditionRepository.findById(original.getId()).orElseThrow();
            assertEquals("Live at-bat", updated.getName());
        }

        @Test
        @DisplayName("PUT /conditions with invalid id -> returns 404")
        void updateNotFound() throws Exception {

            String updateRequest = """
                    {
                        "id"   : 999999,
                        "name" : "Live at-bat"
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
                        "message": "ConditionService. Could not update Condition by id: 999999"
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
        @DisplayName("DELETE /conditions/{id} -> deletes the Condition")
        void delete() throws Exception {
            Condition condition = conditionRepository.save(Condition.builder()
                    .name("Hitting from T")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + condition.getId())
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

            assertFalse(conditionRepository.existsById(condition.getId()));
        }

        @Test
        @DisplayName("DELETE /conditions/{id} with unknown ID -> returns 404")
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
                        "message": "ConditionService. Could not delete id: 999999"
                    }
                    """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }
}
