package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.TeamRepository;
import com.biolab.launchpad.internal.repository.model.Organisation;
import com.biolab.launchpad.internal.repository.model.Team;
import com.biolab.launchpad.internal.repository.model.SportDictionary;
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
@DisplayName("Team Integration Tests")
class TeamControllerIntegrationTest {

    private static final String API = "/api/v1/teams";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    EntityFactory factory;

    SportDictionary sportDictionary;
    Organisation organisation;

    @BeforeEach
    void setUp() {
        sportDictionary = factory.createSportDictionary("coker");
        organisation    = factory.createOrganisation("org");
    }

    @AfterEach
    void tearDown() {
        teamRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /teams -> creates and returns the new Team")
        void create() throws Exception {

            String request =
                            """ 
                                {
                                    "name"           : "avg_team",
                                    "organisationId" : %d,
                                    "sport"          : "%s",
                                    "description"    : "desc"
                                }
                            """.formatted(organisation.getId(), sportDictionary.getName());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int teamId = responseNode.get("id").asInt();
            assertThat(teamId).isPositive();


            String expectedResponse =
                    """ 
                            {
                                        "id"             : %d,
                                        "name"           : "avg_team",
                                        "organisationId" : %d,
                                        "sport"          : "%s",
                                        "description"    : "desc"
                                    }
                            """.formatted(teamId, organisation.getId(), sportDictionary.getName());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /teams with validation message -> returns 422")
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
                                "message"      : "Validation failed: name: Name cannot be blank, and organisationId: Team organisationId cannot be null, and sport: Team sport cannot be null"
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
        @DisplayName("GET /teams -> returns all teams")
        void getAll() throws Exception {

            Team team1 = teamRepository.save(Team.builder()
                    .name("avg_team")
                    .organisationId(organisation.getId())
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            Team team2 = teamRepository.save(Team.builder()
                    .name("max_team")
                    .organisationId(organisation.getId())
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
                        "name"           : "avg_team",
                        "organisationId" : %d,
                        "sport"          : "%s",
                        "description"    : "desc"
                        
                    },
                    {
                        "id"             : %d,
                        "name"           : "max_team",
                        "organisationId" : %d,
                        "sport"          : "%s",
                        "description"    : "desc"
                    }
                ]
                """.formatted(team1.getId(), organisation.getId(), sportDictionary.getId(), team2.getId(), organisation.getId(), sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /teams/{id} -> returns team by ID")
        void getById() throws Exception {

            Team team = teamRepository.save(Team.builder()
                    .name("avg_team")
                    .organisationId(organisation.getId())
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + team.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"             : %d,
                    "name"           : "avg_team",
                    "organisationId" : %d,
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(team.getId(), organisation.getId() ,sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /teams/{id} with unknown ID -> returns 404")
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
                    "message": "Team not found by id: 999999"
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
        @DisplayName("PUT /teams -> updates and returns the Team")
        void update() throws Exception {
            Team original = teamRepository.save(Team.builder()
                    .name("avg_team")
                    .organisationId(organisation.getId())
                    .sport(sportDictionary.getId())
                    .build());

            String updateRequest = """
                {
                    "id"             : %d,
                    "name"           : "updated_velocity",
                    "organisationId" : %d,
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(original.getId(), organisation.getId(), sportDictionary.getId());

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
                    "name"           : "updated_velocity",
                    "organisationId" : %d,
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(original.getId(), organisation.getId(), sportDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            Team updated = teamRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated_velocity", updated.getName());

        }

        @Test
        @DisplayName("PUT /teams with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"             : 999999,
                    "name"           : "updated_velocity",
                    "organisationId" : %d,
                    "sport"          : "%s",
                    "description"    : "desc"
                }
                """.formatted(organisation.getId(), sportDictionary.getId());

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
                    "message": "TeamService. Could not update Team by id: 999999"
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
        @DisplayName("DELETE /teams/{id} -> deletes the Team")
        void delete() throws Exception {
            Team team = teamRepository.save(Team.builder()
                    .name("avg_team")
                    .organisationId(organisation.getId())
                    .sport(sportDictionary.getId())
                    .description("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + team.getId())
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

            assertFalse(teamRepository.existsById(team.getId()));
        }

        @Test
        @DisplayName("DELETE /teams/{id} with unknown ID -> returns 404")
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
                    "message": "TeamService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}