package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.PlayerRepository;
import com.biolab.launchpad.internal.repository.model.Player;
import com.biolab.launchpad.internal.repository.model.Team;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PlayerController Integration Tests")
class PlayerControllerIntegrationTest_ {

    private static final String API = "/api/v1/players";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    EntityFactory factory;

    Team team;

    @BeforeEach
    void setUp() {
        team = factory.createTeam("team_main");
    }

    @AfterEach
    void tearDown() {
        playerRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /players -> creates and returns the new Player")
        void create() throws Exception {

            String request =
                    """ 
                                {
                                    "name"           : "avg_exit_team",
                                    "teamId"         : %d,
                                    "graduationYear" : 2020,
                                    "dob"            : "1990-05-15"
                                }
                            """.formatted(team.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int playerId = responseNode.get("id").asInt();
            assertThat(playerId).isPositive();


            String expectedResponse =
                    """ 
                            {
                                        "id"             : %d,
                                        "name"           : "avg_exit_team",
                                        "teamId"         : %d,
                                        "graduationYear" : 2020,
                                        "dob"            : "1990-05-15"
                                    }
                            """.formatted(playerId, team.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /players with validation message -> returns 422")
        void createValidationError() throws Exception {

            String request =
                    """
                                {
                                    "graduationYear"  : 2020
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
                                "message"        : "Validation failed: name: Name cannot be blank, and teamId: Player teamId cannot be null"
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
        @DisplayName("GET /players -> returns all players")
        void getAll() throws Exception {

            Player player1 = playerRepository.save(Player.builder()
                    .name("avg_exit_team")
                    .teamId(team.getId())
                    .graduationYear(2020)
                    .dob(LocalDate.of(1990, 5, 15))
                    .build());

            Player player2 = playerRepository.save(Player.builder()
                    .name("max_entry_velocity")
                    .teamId(team.getId())
                    .graduationYear(2020)
                    .dob(LocalDate.of(1990, 5, 15))
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
                        "name"           : "avg_exit_team",
                        "teamId"         : %d,
                        "graduationYear" : 2020,
                        "dob"            : "1990-05-15"
                    },
                    {
                        "id"             : %d,
                        "name"           : "max_entry_velocity",
                        "teamId"         : %d,
                        "graduationYear" : 2020,
                        "dob"            : "1990-05-15"
                    }
                ]
                """.formatted(player1.getId(), team.getId(), player2.getId(), team.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /players/{id} -> returns player by ID")
        void getById() throws Exception {

            Player player = playerRepository.save(Player.builder()
                    .name("avg_exit_team")
                    .teamId(team.getId())
                    .graduationYear(2020)
                    .dob(LocalDate.of(1990, 5, 15))
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + player.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"             : %d,
                    "name"           : "avg_exit_team",
                    "teamId"         : %d,
                    "graduationYear" : 2020,
                    "dob"            : "1990-05-15"
                }
                """.formatted(player.getId(), team.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /players/{id} with unknown ID -> returns 404")
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
                    "message": "Player not found by id: 999999"
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
        @DisplayName("PUT /players -> updates and returns the Player")
        void update() throws Exception {
            Player original = playerRepository.save(Player.builder()
                    .name("avg_exit_team")
                    .teamId(team.getId())
                    .graduationYear(2020)
                    .dob(LocalDate.of(1990, 5, 15))
                    .build());

            String updateRequest = """
                {
                    "id"             : %d,
                    "name"           : "upd",
                    "teamId"         : %d,
                    "graduationYear" : 2021,
                    "dob"            : "1990-05-16"
                }
                """.formatted(original.getId(), team.getId());

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
                    "name"           : "upd",
                    "teamId"         : %d,
                    "graduationYear" : 2021,
                    "dob"            : "1990-05-16"
                }
                """.formatted(original.getId(), team.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            Player updated = playerRepository.findById(original.getId()).orElseThrow();
            assertEquals("upd"        , updated.getName());
            assertEquals(2021         , updated.getGraduationYear());
            assertEquals("1990-05-16" , updated.getDob().toString());

        }

        @Test
        @DisplayName("PUT /players with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"            : 999999,
                    "name"          : "upd",
                    "teamId"        : %d
                }
                """.formatted(team.getId());

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
                    "message": "PlayerService. Could not update Player by id: 999999"
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
        @DisplayName("DELETE /players/{id} -> deletes the Player")
        void delete() throws Exception {
            Player player = playerRepository.save(Player.builder()
                    .name("avg_exit_team")
                    .teamId(team.getId())
                    .graduationYear(2020)
                    .dob(LocalDate.of(1990, 5, 15))
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + player.getId())
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

            assertFalse(playerRepository.existsById(player.getId()));
        }

        @Test
        @DisplayName("DELETE /players/{id} with unknown ID -> returns 404")
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
                    "message": "PlayerService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}