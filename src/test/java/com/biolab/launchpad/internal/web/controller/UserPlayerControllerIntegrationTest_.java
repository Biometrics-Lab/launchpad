package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.UserPlayerRepository;
import com.biolab.launchpad.internal.repository.model.Player;
import com.biolab.launchpad.internal.repository.model.User;
import com.biolab.launchpad.internal.repository.model.UserPlayer;
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
@DisplayName("UserPlayerController Integration Tests")
class UserPlayerControllerIntegrationTest_ {

    private static final String API = "/api/v1/userPlayers";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserPlayerRepository userPlayerRepository;

    @Autowired
    EntityFactory factory;

    User   user;
    Player player;

    @BeforeEach
    void setUp() {
        user   = factory.createUser("Us");
        player = factory.createPlayer("Pl1");
    }

    @AfterEach
    void tearDown() {
        userPlayerRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /userPlayers -> creates and returns the new UserPlayer")
        void create() throws Exception {

            String request =
                    """ 
                                {
                                    "userId"          : %d,
                                    "playerId"        : %d
                                }
                            """.formatted(user.getId(), player.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int userPlayerId = responseNode.get("id").asInt();
            assertThat(userPlayerId).isPositive();


            String expectedResponse =
                                    """ 
                                    {
                                        "id"           : %d,
                                        "userId"       : %d,
                                        "playerId"     : %d
                                    }
                                    """.formatted(userPlayerId, user.getId(), player.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /userPlayers with validation message -> returns 422")
        void createValidationError() throws Exception {

            String request =
                            """
                                {
                                    "role" : null
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
                                "message"      : "Validation failed: playerId: UserPlayer playerId cannot be null, and userId: UserPlayer userId cannot be null"
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
        @DisplayName("GET /userPlayers -> returns all userPlayers")
        void getAll() throws Exception {

            UserPlayer userPlayer1 = userPlayerRepository.save(UserPlayer.builder()
                    .userId(user.getId())
                    .playerId(player.getId())
                    .build());

            UserPlayer userPlayer2 = userPlayerRepository.save(UserPlayer.builder()
                    .userId(user.getId())
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
                        "id"           : %d,
                        "userId"       : %d,
                        "playerId"     : %d
                    },
                    {
                        "id"           : %d,
                        "userId"       : %d,
                        "playerId"     : %d
                    }
                ]
                """.formatted(userPlayer1.getId(), user.getId(), player.getId(), userPlayer2.getId(), user.getId(), player.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /userPlayers/{id} -> returns userPlayer by ID")
        void getById() throws Exception {

            UserPlayer userPlayer = userPlayerRepository.save(UserPlayer.builder()
                    .userId(user.getId())
                    .playerId(player.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + userPlayer.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse =
                    """ 
                    {
                        "id"           : %d,
                        "userId"       : %d,
                        "playerId"     : %d
                    }
                    """.formatted(userPlayer.getId(), user.getId(), player.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /userPlayers/{id} with unknown ID -> returns 404")
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
                    "message": "UserPlayer not found by id: 999999"
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
        @DisplayName("PUT /userPlayers -> updates and returns the UserPlayer")
        void update() throws Exception {
            UserPlayer original = userPlayerRepository.save(UserPlayer.builder()
                    .userId(user.getId())
                    .playerId(player.getId())
                    .build());

            user.setName("UsUp");
            String updateRequest =
                    """ 
                    {
                        "id"           : %d,
                        "userId"       : %d,
                        "playerId"     : %d
                    }
                    """.formatted(original.getId(), user.getId(), player.getId());

            String jsonResponse = mvc.perform(
                            put(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(updateRequest)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            String expectedResponse =
                    """ 
                    {
                        "id"           : %d,
                        "userId"       : %d,
                        "playerId"     : %d
                    }
                    """.formatted(original.getId(), user.getId(), player.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            UserPlayer updated = userPlayerRepository.findById(original.getId()).orElseThrow();

            user.setName("Us");

        }

        @Test
        @DisplayName("PUT /userPlayers with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest =  """ 
                    {
                        "id"           : 999999,
                        "userId"       : %d,
                        "playerId"     : %d
                    }
                    """.formatted(user.getId(), player.getId());

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
                    "message": "UserPlayerService. Could not update UserPlayer by id: 999999"
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
        @DisplayName("DELETE /userPlayers/{id} -> deletes the UserPlayer")
        void delete() throws Exception {
            UserPlayer userPlayer = userPlayerRepository.save(UserPlayer.builder()
                    .userId(user.getId())
                    .playerId(player.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + userPlayer.getId())
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

            assertFalse(userPlayerRepository.existsById(userPlayer.getId()));
        }

        @Test
        @DisplayName("DELETE /userPlayers/{id} with unknown ID -> returns 404")
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
                    "message": "UserPlayerService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }
}