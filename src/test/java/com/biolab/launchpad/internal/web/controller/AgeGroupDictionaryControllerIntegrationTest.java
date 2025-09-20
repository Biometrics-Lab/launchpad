package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.AgeGroupDictionary;
import com.biolab.launchpad.internal.repository.AgeGroupDictionaryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AgeGroupDictionaryController Integration Tests")
class AgeGroupDictionaryControllerIntegrationTest {

    private static final String API = "/api/v1/ageGroupDictionarys";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AgeGroupDictionaryRepository ageGroupDictionaryRepository;

    @AfterEach
    void tearDown() {
        ageGroupDictionaryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /ageGroupDictionarys -> creates and returns the new AgeGroupDictionary")
        void create() throws Exception {

            String request =
                    """
                                {
                                    "name" : "AgeGroup"
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

            String ageGroupDictionaryId = responseNode.get("id").asText();
            assertThat(ageGroupDictionaryId).isNotBlank();


            String expectedResponse =
                    """
                            {
                                        "id"           : %d,
                                        "name"         : "AgeGroup"
                                    }
                            """.formatted(ageGroupDictionaryId);

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /ageGroupDictionarys with validation message -> returns 422")
        void createValidationError() throws Exception {

            String request =
                    """
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

            String expectedResponse =
                    """
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
        @DisplayName("GET /ageGroupDictionarys -> returns all ageGroupDictionarys")
        void getAll() throws Exception {

            AgeGroupDictionary ageGroupDictionary1 = ageGroupDictionaryRepository.save(AgeGroupDictionary.builder()
                    .name("avg_launch_angle")
                    .build());

            AgeGroupDictionary ageGroupDictionary2 = ageGroupDictionaryRepository.save(AgeGroupDictionary.builder()
                    .name("max_launch_angle")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                [
                    {
                        "id"           : %d,
                        "name"         : "avg_ageGroup"
                    },
                    {
                        "id"           : %d,
                        "name"         : "max_ageGroup"
                    }
                ]
                """.formatted(ageGroupDictionary1.getId(), ageGroupDictionary2.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /ageGroupDictionarys/{id} -> returns ageGroupDictionary by ID")
        void getById() throws Exception {

            AgeGroupDictionary ageGroupDictionary = ageGroupDictionaryRepository.save(AgeGroupDictionary.builder()
                    .name("ageGroup")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + ageGroupDictionary.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"           : %d,
                    "name"         : "ageGroup"
                }
                """.formatted(ageGroupDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /ageGroupDictionarys/{id} with unknown ID -> returns 404")
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
                    "message": "AgeGroupDictionary not found by id: 999999"
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
        @DisplayName("PUT /ageGroupDictionarys -> updates and returns the AgeGroupDictionary")
        void update() throws Exception {
            AgeGroupDictionary original = ageGroupDictionaryRepository.save(AgeGroupDictionary.builder()
                    .name("ageGroup")
                    .build());

            String updateRequest = """
                {
                    "id"            : %d,
                    "name"          : "updated_ageGroup"
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
                    "id"           : %d,
                    "name"         : "updated_ageGroup
                }
                """.formatted(original.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            AgeGroupDictionary updated = ageGroupDictionaryRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated_ageGroup", updated.getName());

        }

        @Test
        @DisplayName("PUT /ageGroupDictionarys with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"            : 999999,
                    "name"          : "updated_ageGroup"
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
                    "message": "AgeGroupDictionaryService. Could not update AgeGroupDictionary by id: 999999"
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
        @DisplayName("DELETE /ageGroupDictionarys/{id} -> deletes the AgeGroupDictionary")
        void delete() throws Exception {
            AgeGroupDictionary ageGroupDictionary = ageGroupDictionaryRepository.save(AgeGroupDictionary.builder()
                    .name("ageGroup")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + ageGroupDictionary.getId())
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

            assertFalse(ageGroupDictionaryRepository.existsById(ageGroupDictionary.getId()));
        }

        @Test
        @DisplayName("DELETE /ageGroupDictionarys/{id} with unknown ID -> returns 404")
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
                    "message": "AgeGroupDictionaryService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}