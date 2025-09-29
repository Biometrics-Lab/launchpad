package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.ResourceTypeDictionary;
import com.biolab.launchpad.internal.repository.ResourceTypeDictionaryRepository;
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
@DisplayName("ResourceTypeDictionaryController Integration Tests")
class ResourceTypeDictionaryControllerIntegrationTest {

    private static final String API = "/api/v1/resource_type_dictionarys";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ResourceTypeDictionaryRepository resourceTypeDictionaryRepository;

    @AfterEach
    void tearDown() {
        resourceTypeDictionaryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /age_group_dictionarys -> creates and returns the new ResourceTypeDictionary")
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

            String resourceTypeDictionaryId = responseNode.get("name").asText();
            assertThat(resourceTypeDictionaryId).isNotBlank();

            String expectedResponse =
                    """
                       {
                            "name" : "AgeGroup",
                            "description" : "other description"
                        }
   """.formatted(resourceTypeDictionaryId);

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
        @DisplayName("GET /age_group_dictionarys -> returns all resourceTypeDictionarys")
        void getAll() throws Exception {

            ResourceTypeDictionary resourceTypeDictionary1 = ResourceTypeDictionary.builder()
                    .name("avg_launch_angle")
                    .description("other description")
                    .build();
            ResourceTypeDictionary resourceTypeDictionary2 = ResourceTypeDictionary.builder()
                    .name("max_launch_angle")
                    .description("other description")
                    .build();

            resourceTypeDictionary1.markAsNew(true);
            resourceTypeDictionary2.markAsNew(true);

            resourceTypeDictionaryRepository.save(resourceTypeDictionary1);
            resourceTypeDictionaryRepository.save(resourceTypeDictionary2);

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
                """.formatted(resourceTypeDictionary1.getName(), resourceTypeDictionary2.getName());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /age_group_dictionarys/{id} -> returns resourceTypeDictionary by ID")
        void getById() throws Exception {

            ResourceTypeDictionary resourceTypeDictionary = ResourceTypeDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            resourceTypeDictionary.markAsNew(true);

            resourceTypeDictionaryRepository.save(resourceTypeDictionary);

            String jsonResponse = mvc.perform(
                            get(API + "/" + resourceTypeDictionary.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                        {
                                             "name" : "%s",
                                             "description" : "other description"
                                         }
                                        """.formatted(resourceTypeDictionary.getName());

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
                    "message": "Resource_type_dictionary not found by id: 999999"
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
        @DisplayName("PUT /age_group_dictionarys -> updates and returns the ResourceTypeDictionary")
        void update() throws Exception {

            ResourceTypeDictionary resourceTypeDictionary = ResourceTypeDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            resourceTypeDictionary.markAsNew(true);

            ResourceTypeDictionary original = resourceTypeDictionaryRepository.save(resourceTypeDictionary);

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

            ResourceTypeDictionary updated = resourceTypeDictionaryRepository.findById(original.getId()).orElseThrow();
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
                    "message": "ResourceTypeDictionaryService. Could not update ResourceTypeDictionary by id: 999999"
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
        @DisplayName("DELETE /age_group_dictionarys/{id} -> deletes the ResourceTypeDictionary")
        void delete() throws Exception {

            ResourceTypeDictionary resourceTypeDictionary = ResourceTypeDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            resourceTypeDictionary.markAsNew(true);

            ResourceTypeDictionary original = resourceTypeDictionaryRepository.save(resourceTypeDictionary);

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + resourceTypeDictionary.getId())
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

            assertFalse(resourceTypeDictionaryRepository.existsById(resourceTypeDictionary.getId()));
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
                    "message": "ResourceTypeDictionaryService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}