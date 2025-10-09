package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.UserRepository;
import com.biolab.launchpad.internal.repository.model.Measurement;
import com.biolab.launchpad.internal.repository.model.User;
import com.biolab.launchpad.internal.repository.model.UserRoleDictionary;
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
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    private static final String API = "/api/v1/users";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityFactory factory;

    UserRoleDictionary userRoleDictionary;

    @BeforeEach
    void setUp() {
        userRoleDictionary = factory.createUserRoleDictionary("Us");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /users -> creates and returns the new User")
        void create() throws Exception {

            String request =
                    """ 
                                {
                                    "name"         : "avg_exit_velocity",
                                    "role"         : "%s"
                                }
                            """.formatted(userRoleDictionary.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int userId = responseNode.get("id").asInt();
            assertThat(userId).isPositive();


            String expectedResponse =
                    """ 
                            {
                                        "id"           : %d,
                                        "name"         : "avg_exit_velocity",
                                        "role"         : "%s"
                                    }
                            """.formatted(userId, userRoleDictionary.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode); // deep comparison without order
        }

        @Test
        @DisplayName("POST /users with validation message -> returns 422")
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
                                "message"      : "Validation failed: name: Name cannot be blank, and role: User role cannot be null"
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
        @DisplayName("GET /users -> returns all users")
        void getAll() throws Exception {

            User user1 = userRepository.save(User.builder()
                    .name("avg_exit_velocity")
                    .role(userRoleDictionary.getId())
                    .build());

            User user2 = userRepository.save(User.builder()
                    .name("max_entry_velocity")
                    .role(userRoleDictionary.getId())
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
                        "name"         : "avg_exit_velocity",
                        "role"         : "%s"
                    },
                    {
                        "id"           : %d,
                        "name"         : "max_entry_velocity",
                        "role"         : "%s"
                    }
                ]
                """.formatted(user1.getId(), userRoleDictionary.getId(), user2.getId(), userRoleDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /users/{id} -> returns user by ID")
        void getById() throws Exception {

            User user = userRepository.save(User.builder()
                    .name("avg_exit_velocity")
                    .role(userRoleDictionary.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + user.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"           : %d,
                    "name"         : "avg_exit_velocity",
                    "role"         : "%s"
                }
                """.formatted(user.getId(), userRoleDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /users/{id} with unknown ID -> returns 404")
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
                    "message": "User not found by id: 999999"
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
        @DisplayName("PUT /users -> updates and returns the User")
        void update() throws Exception {
            User original = userRepository.save(User.builder()
                    .name("avg_exit_velocity")
                    .role(userRoleDictionary.getId())
                    .build());

            String updateRequest = """
                {
                    "id"            : %d,
                    "name"          : "updated_velocity",
                    "role"          : "%s"
                }
                """.formatted(original.getId(), userRoleDictionary.getId());

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
                    "id"           : %d,
                    "name"         : "updated_velocity",
                    "role"         : "%s"
                }
                """.formatted(original.getId(), userRoleDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            User updated = userRepository.findById(original.getId()).orElseThrow();
            assertEquals("updated_velocity", updated.getName());

        }

        @Test
        @DisplayName("PUT /users with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"            : 999999,
                    "name"          : "updated_velocity",
                    "role"          : "%s"
                }
                """.formatted(userRoleDictionary.getId());

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
                    "message": "UserService. Could not update User by id: 999999"
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
        @DisplayName("DELETE /users/{id} -> deletes the User")
        void delete() throws Exception {
            User user = userRepository.save(User.builder()
                    .name("avg_exit_velocity")
                    .role(userRoleDictionary.getId())
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + user.getId())
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

            assertFalse(userRepository.existsById(user.getId()));
        }

        @Test
        @DisplayName("DELETE /users/{id} with unknown ID -> returns 404")
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
                    "message": "UserService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }
}