package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.ModelRepository;
import com.biolab.launchpad.internal.repository.model.AgeGroupDictionary;
import com.biolab.launchpad.internal.repository.model.SportDictionary;
import com.biolab.launchpad.internal.repository.model.Model;
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
@DisplayName("Model Integration Tests")
class ModelControllerIntegrationTest {

    private static final String API = "/api/v1/models";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ModelRepository modelRepository;

    @Autowired
    EntityFactory factory;

    AgeGroupDictionary ageGroupDictionary;
    SportDictionary sportDictionary;

    @BeforeEach
    void setUp() {
        ageGroupDictionary = factory.createAgeGroupDictionary("age");
        sportDictionary    = factory.createSportDictionary("coker");
    }

    @AfterEach
    void tearDown() {
        modelRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /models -> creates and returns the new Model")
        void create() throws Exception {

            String request =
                            """ 
                                {
                                    "age_group"         : "%s",
                                    "sport"             : "%s",
                                    "description"       : "desc"
                                }
                            """.formatted(ageGroupDictionary.getId(), sportDictionary.getName());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int modelId = responseNode.get("id").asInt();
            assertThat(modelId).isPositive();


            String expectedResponse =
                    """ 
                            {
                                        "id"                : %d,
                                        "age_group"         : "%s",
                                        "sport"             : "%s",
                                        "description"       : "desc"
                                    }
                            """.formatted(modelId, ageGroupDictionary.getId(), sportDictionary.getName());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /models with validation message -> returns 422")
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
                                "message"      : "Validation failed: age_group: Model age_group cannot be null, and sport: Model sport cannot be null"
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
        @DisplayName("GET /models -> returns all models")
        void getAll() throws Exception {

            Model model1 = modelRepository.save(Model.builder()
                    .age_group(ageGroupDictionary.getId())
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            Model model2 = modelRepository.save(Model.builder()
                    .age_group(ageGroupDictionary.getId())
                    .sport(sportDictionary.getId())
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
                        "age_group"      : "%s",
                        "sport"          : "%s",
                        "description"    : "desc"
                    },
                    {
                        "id"             : %d,
                        "age_group"      : "%s",
                        "sport"          : "%s",
                        "description"    : "desc"
                    }
                ]
                """.formatted(model1.getId(), ageGroupDictionary.getId(), sportDictionary.getId(), model2.getId(), ageGroupDictionary.getId(), sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /models/{id} -> returns model by ID")
        void getById() throws Exception {

            Model model = modelRepository.save(Model.builder()
                    .age_group(ageGroupDictionary.getId())
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + model.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"             : %d,
                    "age_group"      : "%s",
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(model.getId(), ageGroupDictionary.getId() ,sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /models/{id} with unknown ID -> returns 404")
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
                    "message": "Model not found by id: 999999"
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
        @DisplayName("PUT /models -> updates and returns the Model")
        void update() throws Exception {
            Model original = modelRepository.save(Model.builder()
                    .age_group(ageGroupDictionary.getId())
                    .sport(sportDictionary.getId())
                    .build());

            String updateRequest = """
                {
                    "id"             : %d,
                    "age_group"      : "%s",
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(original.getId(), ageGroupDictionary.getId(), sportDictionary.getId());

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
                    "age_group"      : "%s",
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(original.getId(), ageGroupDictionary.getId(), sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            Model updated = modelRepository.findById(original.getId()).orElseThrow();

        }

        @Test
        @DisplayName("PUT /models with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"             : 999999,
                    "age_group"      : "%s",
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(ageGroupDictionary.getId(), sportDictionary.getId());

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
                    "message": "ModelService. Could not update Model by id: 999999"
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
        @DisplayName("DELETE /models/{id} -> deletes the Model")
        void delete() throws Exception {
            Model model = modelRepository.save(Model.builder()
                    .age_group(ageGroupDictionary.getId())
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + model.getId())
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

            assertFalse(modelRepository.existsById(model.getId()));
        }

        @Test
        @DisplayName("DELETE /models/{id} with unknown ID -> returns 404")
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
                    "message": "ModelService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}