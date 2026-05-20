package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.AssessmentTemplateRepository;
import com.biolab.launchpad.internal.repository.model.SportDictionary;
import com.biolab.launchpad.internal.repository.model.AssessmentTemplate;
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

import lombok.extern.log4j.Log4j2;
@Log4j2
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AssessmentTemplate Integration Tests")
class AssessmentTemplateControllerIntegrationTest {

    private static final String API = "/api/v1/assessmentTemplates";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AssessmentTemplateRepository assessmentTemplateRepository;

    @Autowired
    EntityFactory factory;

    SportDictionary sportDictionary;

    @BeforeEach
    void setUp() {
        sportDictionary = factory.createSportDictionary("coker");
    }

    @AfterEach
    void tearDown() {
        assessmentTemplateRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /assessmentTemplates -> creates and returns the new AssessmentTemplate")
        void create() throws Exception {

            String request =
                    """ 
                                {
                                    "name"         : "avg_exit_velocity",
                                    "sport"        : "%s",
                                    "description"  : "desc"
                                }
                            """.formatted(sportDictionary.getName());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int assessmentTemplateId = responseNode.get("id").asInt();
            assertThat(assessmentTemplateId).isPositive();


            String expectedResponse =
                    """
                            {
                                        "id"                  : %d,
                                        "name"                : "avg_exit_velocity",
                                        "sport"               : "%s",
                                        "description"         : "desc",
                                        "conditionId"         : null,
                                        "allowExternalUrls"   : false
                                    }
                            """.formatted(assessmentTemplateId, sportDictionary.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /assessmentTemplates with validation message -> returns 422")
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
                                            "status"   : 422,
                                            "message"  : "Validation failed: name: Name cannot be blank, and sport: AssessmentTemplate sport cannot be null"
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
        @DisplayName("GET /assessmentTemplates -> returns all assessmentTemplates")
        void getAll() throws Exception {

            AssessmentTemplate assessmentTemplate1 = assessmentTemplateRepository.save(AssessmentTemplate.builder()
                    .name("avg_exit_velocity")
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            AssessmentTemplate assessmentTemplate2 = assessmentTemplateRepository.save(AssessmentTemplate.builder()
                    .name("max_entry_velocity")
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
                            "id"                  : %d,
                            "name"                : "avg_exit_velocity",
                            "sport"               : "%s",
                            "description"         : "desc",
                            "conditionId"         : null,
                            "allowExternalUrls"   : false
                        },
                        {
                            "id"                  : %d,
                            "name"                : "max_entry_velocity",
                            "sport"               : "%s",
                            "description"         : "desc",
                            "conditionId"         : null,
                            "allowExternalUrls"   : false
                        }
                    ]
                    """.formatted(assessmentTemplate1.getId(), sportDictionary.getId(), assessmentTemplate2.getId(), sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessmentTemplates/{id} -> returns assessmentTemplate by ID")
        void getById() throws Exception {

            AssessmentTemplate assessmentTemplate = assessmentTemplateRepository.save(AssessmentTemplate.builder()
                    .name("avg_exit_velocity")
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + assessmentTemplate.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"                  : %d,
                    "name"                : "avg_exit_velocity",
                    "sport"               : "%s",
                    "description"         : "desc",
                    "conditionId"         : null,
                    "allowExternalUrls"   : false
                }
                """.formatted(assessmentTemplate.getId(), sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessmentTemplates/{id} with unknown ID -> returns 404")
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
                    "message": "AssessmentTemplate not found by id: 999999"
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
        @DisplayName("PUT /assessmentTemplates -> updates and returns the AssessmentTemplate")
        void update() throws Exception {
            AssessmentTemplate original = assessmentTemplateRepository.save(AssessmentTemplate.builder()
                    .name("avg_exit_velocity")
                    .sport(sportDictionary.getId())
                    .build());

            String updateRequest = """
                {
                    "id"            : %d,
                    "name"          : "updated_velocity",
                    "sport"         : "%s",
                    "description"  : "desc"
                }
                """.formatted(original.getId(), sportDictionary.getId());

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
                    "id"                  : %d,
                    "name"                : "updated_velocity",
                    "sport"               : "%s",
                    "description"         : "desc",
                    "conditionId"         : null,
                    "allowExternalUrls"   : false
                }
                """.formatted(original.getId(), sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            AssessmentTemplate updated = assessmentTemplateRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated_velocity", updated.getName());

        }

        @Test
        @DisplayName("PUT /assessmentTemplates with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"            : 999999,
                    "name"          : "updated_velocity",
                    "sport"         : "%s",
                    "description"  : "desc"
                }
                """.formatted(sportDictionary.getId());

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
                    "message": "AssessmentTemplateService. Could not update AssessmentTemplate by id: 999999"
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
        @DisplayName("DELETE /assessmentTemplates/{id} -> deletes the AssessmentTemplate")
        void delete() throws Exception {
            AssessmentTemplate assessmentTemplate = assessmentTemplateRepository.save(AssessmentTemplate.builder()
                    .name("avg_exit_velocity")
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + assessmentTemplate.getId())
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

            assertFalse(assessmentTemplateRepository.existsById(assessmentTemplate.getId()));
        }

        @Test
        @DisplayName("DELETE /assessmentTemplates/{id} with unknown ID -> returns 404")
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
                    "message": "AssessmentTemplateService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}