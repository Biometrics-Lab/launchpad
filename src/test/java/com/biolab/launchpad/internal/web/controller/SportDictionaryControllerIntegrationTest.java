package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.SportDictionary;
import com.biolab.launchpad.internal.repository.SportDictionaryRepository;
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

import static graphql.Assert.assertFalse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("SportDictionaryController Integration Tests")
class SportDictionaryControllerIntegrationTest {

    private static final String API = "/api/v1/sport_dictionarys";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SportDictionaryRepository sportDictionaryRepository;

    @AfterEach
    void tearDown() {
        sportDictionaryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /age_group_dictionarys -> creates and returns the new SportDictionary")
        void create() throws Exception {

            String request =
                    """
                        {
                            "name" : "AgeGroup",
                            "description" : "other description"
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

            String sportDictionaryId = responseNode.get("name").asText();
            assertThat(sportDictionaryId).isNotBlank();

            String expectedResponse =
                    """
                       {
                            "name" : "AgeGroup",
                            "description" : "other description"
                        }
   """.formatted(sportDictionaryId);

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /age_group_dictionarys with validation message -> returns 422")
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
        @DisplayName("GET /age_group_dictionarys -> returns all sportDictionarys")
        void getAll() throws Exception {

            SportDictionary sportDictionary1 = SportDictionary.builder()
                    .name("avg_launch_angle")
                    .description("other description")
                    .build();
            SportDictionary sportDictionary2 = SportDictionary.builder()
                    .name("max_launch_angle")
                    .description("other description")
                    .build();

            sportDictionary1.markAsNew(true);
            sportDictionary2.markAsNew(true);

            sportDictionaryRepository.save(sportDictionary1);
            sportDictionaryRepository.save(sportDictionary2);

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
                        "name"           : "%s",
                        "description" : "other description"
                    },
                    {
                        "name"           : "%s",
                        "description" : "other description"
                    }
                ]
                """.formatted(sportDictionary1.getName(), sportDictionary2.getName());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /age_group_dictionarys/{id} -> returns sportDictionary by ID")
        void getById() throws Exception {

            SportDictionary sportDictionary = SportDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            sportDictionary.markAsNew(true);

            sportDictionaryRepository.save(sportDictionary);

            String jsonResponse = mvc.perform(
                            get(API + "/" + sportDictionary.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                        {
                                             "name" : "%s",
                                             "description" : "other description"
                                         }
                                        """.formatted(sportDictionary.getName());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /age_group_dictionarys/{id} with unknown ID -> returns 404")
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
                    "message": "Sport_dictionary not found by id: 999999"
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
        @DisplayName("PUT /age_group_dictionarys -> updates and returns the SportDictionary")
        void update() throws Exception {

            SportDictionary sportDictionary = SportDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            sportDictionary.markAsNew(true);

            SportDictionary original = sportDictionaryRepository.save(sportDictionary);

            String updateRequest = """
                                    {
                                             "name" : "%s",
                                             "description" : "updated other description"
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
                                             "name" : "%s",
                                             "description" : "updated other description"
                                        }
                                       """.formatted(original.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            SportDictionary updated = sportDictionaryRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated other description", updated.getDescription());

        }

        @Test
        @DisplayName("PUT /age_group_dictionarys with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                    {
                                          "name" : "999999",
                                          "description" : "updated other description"
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
                    "message": "SportDictionaryService. Could not update SportDictionary by id: 999999"
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
        @DisplayName("DELETE /age_group_dictionarys/{id} -> deletes the SportDictionary")
        void delete() throws Exception {

            SportDictionary sportDictionary = SportDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            sportDictionary.markAsNew(true);

            SportDictionary original = sportDictionaryRepository.save(sportDictionary);

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + sportDictionary.getId())
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

            assertFalse(sportDictionaryRepository.existsById(sportDictionary.getId()));
        }

        @Test
        @DisplayName("DELETE /age_group_dictionarys/{id} with unknown ID -> returns 404")
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
                    "message": "SportDictionaryService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}