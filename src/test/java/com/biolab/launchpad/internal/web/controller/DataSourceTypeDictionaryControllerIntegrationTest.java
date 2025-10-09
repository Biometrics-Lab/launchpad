package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.DataSourceTypeDictionaryRepository;
import com.biolab.launchpad.internal.repository.model.DataSourceTypeDictionary;
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
@DisplayName("DataSourceTypeDictionaryController Integration Tests")
class DataSourceTypeDictionaryControllerIntegrationTest {

    private static final String API = "/api/v1/dataSourceTypeDictionarys";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DataSourceTypeDictionaryRepository dataSourceTypeDictionaryRepository;

    @AfterEach
    void tearDown() {
        dataSourceTypeDictionaryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /dataSourceTypeDictionarys -> creates and returns the new DataSourceTypeDictionary")
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

            String dataSourceTypeDictionaryId = responseNode.get("name").asText();
            assertThat(dataSourceTypeDictionaryId).isNotBlank();

            String expectedResponse =
                    """
                       {
                            "name"        : "AgeGroup",
                            "description" : "other description"
                        }
   """.formatted(dataSourceTypeDictionaryId);

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /dataSourceTypeDictionarys with validation message -> returns 422")
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
        @DisplayName("GET /dataSourceTypeDictionarys -> returns all dataSourceTypeDictionarys")
        void getAll() throws Exception {

            DataSourceTypeDictionary dataSourceTypeDictionary1 = DataSourceTypeDictionary.builder()
                    .name("avg_launch_angle")
                    .description("other description")
                    .build();
            DataSourceTypeDictionary dataSourceTypeDictionary2 = DataSourceTypeDictionary.builder()
                    .name("max_launch_angle")
                    .description("other description")
                    .build();

            dataSourceTypeDictionary1.markAsNew(true);
            dataSourceTypeDictionary2.markAsNew(true);

            dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary1);
            dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary2);

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
                            "name"        : "%s",
                            "description" : "other description"
                        },
                        {
                            "name"        : "%s",
                            "description" : "other description"
                        }
                    ]
                    """.formatted(dataSourceTypeDictionary1.getName(), dataSourceTypeDictionary2.getName());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /dataSourceTypeDictionarys/{id} -> returns dataSourceTypeDictionary by ID")
        void getById() throws Exception {

            DataSourceTypeDictionary dataSourceTypeDictionary = DataSourceTypeDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            dataSourceTypeDictionary.markAsNew(true);

            dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary);

            String jsonResponse = mvc.perform(
                            get(API + "/" + dataSourceTypeDictionary.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                        {
                                             "name"        : "%s",
                                             "description" : "other description"
                                         }
                                        """.formatted(dataSourceTypeDictionary.getName());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /dataSourceTypeDictionarys/{id} with unknown ID -> returns 404")
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
                        "message": "DataSourceTypeDictionary not found by id: 999999"
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
        @DisplayName("PUT /dataSourceTypeDictionarys -> updates and returns the DataSourceTypeDictionary")
        void update() throws Exception {

            DataSourceTypeDictionary dataSourceTypeDictionary = DataSourceTypeDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            dataSourceTypeDictionary.markAsNew(true);

            DataSourceTypeDictionary original = dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary);

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

            DataSourceTypeDictionary updated = dataSourceTypeDictionaryRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated other description", updated.getDescription());

        }

        @Test
        @DisplayName("PUT /dataSourceTypeDictionarys with invalid id -> returns 404")
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
                        "message": "DataSourceTypeDictionaryService. Could not update DataSourceTypeDictionary by id: 999999"
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
        @DisplayName("DELETE /dataSourceTypeDictionarys/{id} -> deletes the DataSourceTypeDictionary")
        void delete() throws Exception {

            DataSourceTypeDictionary dataSourceTypeDictionary = DataSourceTypeDictionary.builder()
                    .name("ageGroup")
                    .description("other description")
                    .build();
            dataSourceTypeDictionary.markAsNew(true);

            DataSourceTypeDictionary original = dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary);

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + dataSourceTypeDictionary.getId())
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

            assertFalse(dataSourceTypeDictionaryRepository.existsById(dataSourceTypeDictionary.getId()));
        }

        @Test
        @DisplayName("DELETE /dataSourceTypeDictionarys/{id} with unknown ID -> returns 404")
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
                    "message": "DataSourceTypeDictionaryService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}