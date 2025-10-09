package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.AssessmentRepository;
import com.biolab.launchpad.internal.repository.model.*;
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
@DisplayName("Assessment Integration Tests")
class AssessmentControllerIntegrationTest {

    private static final String API = "/api/v1/assessments";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    EntityFactory factory;

    AssessmentTemplate template;
    SportDictionary     sport;
    Player              player;

    @BeforeEach
    void setUp() {
        template = factory.createAssessmentTemplate("height");
        sport   = factory.createSportDictionary("basketball");
        player   = factory.createPlayer("pl2");
    }

    @AfterEach
    void tearDown() {
        assessmentRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /assessments -> creates and returns the new Assessment")
        void create() throws Exception {

            String request =
                            """ 
                                {
                                    "templateId" : %d,
                                    "sport"      : "%s",
                                    "playerId"   : %d
                                }
                            """.formatted(template.getId(), sport.getId(), player.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int assessmentId = responseNode.get("id").asInt();
            assertThat(assessmentId).isPositive();


            String expectedResponse =
                                    """
                                      {
                                        "id"         : %d,
                                        "templateId" : %d,
                                        "sport"      : "%s",
                                        "playerId"   : %d
                                      }
                                    """.formatted(assessmentId, template.getId(), sport.getId(), player.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /assessments with validation message -> returns 422")
        void createValidationError() throws Exception {
            String request =
                            """
                                {
                                    "description" : ""
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
                                "message"      : "Validation failed: playerId: Assessment playerId cannot be null, and sport: Assessment sport cannot be null, and templateId: Assessment templateId cannot be null"
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
        @DisplayName("GET /assessments -> returns all assessments")
        void getAll() throws Exception {

            Assessment assessment1 = assessmentRepository.save(Assessment.builder()
                    .templateId(template.getId())
                    .sport(sport.getId())
                    .playerId(player.getId())
                    .build());

            Assessment assessment2 = assessmentRepository.save(Assessment.builder()
                    .templateId(template.getId())
                    .sport(sport.getId())
                    .playerId(player.getId())
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
                        "templateId" : %d,
                        "sport"      : "%s",
                        "playerId"   : %d
                    },
                    {
                        "id"         : %d,
                        "templateId" : %d,
                        "sport"      : "%s",
                        "playerId"   : %d
                    }
                ]
                """.formatted(assessment1.getId(), template.getId(), sport.getId(), player.getId(), assessment2.getId(),template.getId(), sport.getId(), player.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessments/{id} -> returns assessment by ID")
        void getById() throws Exception {

            Assessment assessment = assessmentRepository.save(Assessment.builder()
                    .templateId(template.getId())
                    .sport(sport.getId())
                    .playerId(player.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + assessment.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                     {
                                        "id"          : %d,
                                         "templateId" : %d,
                                         "sport"      : "%s",
                                         "playerId"   : %d
                                     }
                                    """.formatted(assessment.getId(), template.getId(), sport.getId(), player.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessments/{id} with unknown ID -> returns 404")
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
                    "message": "Assessment not found by id: 999999"
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
        @DisplayName("PUT /assessments -> updates and returns the Assessment")
        void update() throws Exception {
            Assessment original = assessmentRepository.save(Assessment.builder()
                    .templateId(template.getId())
                    .sport(sport.getId())
                    .playerId(player.getId())
                    .build());

            sport = factory.createSportDictionary("updated");
            String updateRequest = """
                                {
                                    "id"          : %d,
                                    "templateId"  : %d,
                                    "sport"       : "%s",
                                    "playerId"    : %d
                                 }
                                """.formatted(original.getId(), template.getId(), sport.getId(), player.getId());

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
                                    "id"          : %d,
                                    "templateId"  : %d,
                                    "sport"       : "%s",
                                    "playerId"    : %d
                                 }
                                """.formatted(original.getId(), template.getId(), sport.getId(), player.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            Assessment updated = assessmentRepository.findById(original.getId()).orElseThrow();
            assertEquals(updated.getSport(), sport.getName());
        }

        @Test
        @DisplayName("PUT /assessments with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"          : 999999,
                                     "templateId"  : %d,
                                     "sport"       : "%s",
                                     "playerId"    : %d
                                 }
                                 """.formatted(template.getId(), sport.getId(), player.getId());

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
                    "message": "AssessmentService. Could not update Assessment by id: 999999"
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
        @DisplayName("DELETE /assessments/{id} -> deletes the Assessment")
        void delete() throws Exception {
            Assessment assessment = assessmentRepository.save(Assessment.builder()
                    .templateId(template.getId())
                    .sport(sport.getId())
                    .playerId(player.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + assessment.getId())
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

            assertFalse(assessmentRepository.existsById(assessment.getId()));
        }

        @Test
        @DisplayName("DELETE /assessments/{id} with unknown ID -> returns 404")
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
                    "message": "AssessmentService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}