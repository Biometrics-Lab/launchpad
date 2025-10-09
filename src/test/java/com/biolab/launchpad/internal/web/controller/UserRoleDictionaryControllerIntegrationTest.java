package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.model.UserRoleDictionary;
import com.biolab.launchpad.internal.repository.UserRoleDictionaryRepository;
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
@DisplayName("UserRoleDictionaryController Integration Tests")
class UserRoleDictionaryControllerIntegrationTest {

    private static final String API = "/api/v1/userRoleDictionaries";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRoleDictionaryRepository userRoleDictionaryRepository;

    @AfterEach
    void tearDown() {
        userRoleDictionaryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /userRoleDictionarys -> creates and returns the new UserRoleDictionary")
        void create() throws Exception {

            String request =
                    """
                        {
                            "name"        : "AgeGroup",
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

            String userRoleDictionaryId = responseNode.get("name").asText();
            assertThat(userRoleDictionaryId).isNotBlank();

            String expectedResponse =
                    """
                       {
                            "name"        : "AgeGroup",
                            "description" : "other description"
                        }
                    """;

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /userRoleDictionarys with validation message -> returns 422")
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
        @DisplayName("GET /userRoleDictionarys -> returns all userRoleDictionarys")
        void getAll() throws Exception {

            UserRoleDictionary userRoleDictionary1 = UserRoleDictionary.builder()
                    .name("avg_launch_angle")
                    .description("other description")
                    .build();
            UserRoleDictionary userRoleDictionary2 = UserRoleDictionary.builder()
                    .name("max_launch_angle")
                    .description("other description")
                    .build();

            userRoleDictionary1.markAsNew(true);
            userRoleDictionary2.markAsNew(true);

            userRoleDictionaryRepository.save(userRoleDictionary1);
            userRoleDictionaryRepository.save(userRoleDictionary2);

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
                """.formatted(userRoleDictionary1.getName(), userRoleDictionary2.getName());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /userRoleDictionarys/{id} -> returns userRoleDictionary by ID")
        void getById() throws Exception {

            UserRoleDictionary userRoleDictionary = UserRoleDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            userRoleDictionary.markAsNew(true);

            userRoleDictionaryRepository.save(userRoleDictionary);

            String jsonResponse = mvc.perform(
                            get(API + "/" + userRoleDictionary.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                        {
                                             "name"        : "%s",
                                             "description" : "other description"
                                         }
                                        """.formatted(userRoleDictionary.getName());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /userRoleDictionarys/{id} with unknown ID -> returns 404")
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
                    "message": "UserRoleDictionary not found by id: 999999"
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
        @DisplayName("PUT /userRoleDictionarys -> updates and returns the UserRoleDictionary")
        void update() throws Exception {

            UserRoleDictionary userRoleDictionary = UserRoleDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            userRoleDictionary.markAsNew(true);

            UserRoleDictionary original = userRoleDictionaryRepository.save(userRoleDictionary);

            String updateRequest = """
                                    {
                                             "name"        : "%s",
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
                                             "name"        : "%s",
                                             "description" : "updated other description"
                                        }
                                       """.formatted(original.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            UserRoleDictionary updated = userRoleDictionaryRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated other description", updated.getDescription());

        }

        @Test
        @DisplayName("PUT /userRoleDictionarys with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                    {
                                          "name"        : "999999",
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
                    "message": "UserRoleDictionaryService. Could not update UserRoleDictionary by id: 999999"
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
        @DisplayName("DELETE /userRoleDictionarys/{id} -> deletes the UserRoleDictionary")
        void delete() throws Exception {

            UserRoleDictionary userRoleDictionary = UserRoleDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            userRoleDictionary.markAsNew(true);

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + userRoleDictionary.getId())
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

            assertFalse(userRoleDictionaryRepository.existsById(userRoleDictionary.getId()));
        }

        @Test
        @DisplayName("DELETE /userRoleDictionarys/{id} with unknown ID -> returns 404")
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
                    "message": "UserRoleDictionaryService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}