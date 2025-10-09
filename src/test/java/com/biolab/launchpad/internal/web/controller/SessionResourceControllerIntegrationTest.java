package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.SessionResourceRepository;
import com.biolab.launchpad.internal.repository.model.Assessment;
import com.biolab.launchpad.internal.repository.model.SessionResource;
import com.biolab.launchpad.internal.repository.model.ResourceTypeDictionary;
import com.biolab.launchpad.internal.repository.model.Session1;
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
@DisplayName("SessionResource Integration Tests")
class SessionResourceControllerIntegrationTest {

    private static final String API = "/api/v1/sessionResources";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    SessionResourceRepository sessionResourceRepository;

    @Autowired
    EntityFactory factory;

    Session1 session1;
    ResourceTypeDictionary resourceTypeDictionary;

    @BeforeEach
    void setUp() {
        session1                = factory.createSession1("session");
        resourceTypeDictionary  = factory.createResourceTypeDictionary("rTypeDictionary");
    }

    @AfterEach
    void tearDown() {
        sessionResourceRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /sessionResources -> creates and returns the new SessionResource")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "session1Id"     : %d,
                                    "type"           : "%s",
                                    "url"            : "desc"
                                }
                            """.formatted(session1.getId(), resourceTypeDictionary.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int sessionResourceId = responseNode.get("id").asInt();
            assertThat(sessionResourceId).isPositive();


            String expectedResponse =
                    """
                            {
                                        "id"             : %d,
                                        "session1Id"     : %d,
                                        "type"           : "%s",
                                        "url"            :"desc"
                                    }
                            """.formatted(sessionResourceId, session1.getId(), resourceTypeDictionary.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /sessionResources with validation message -> returns 422")
        void createValidationError() throws Exception {
            String request =
                            """
                                {
                                    "url" : "desc"
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
                                "message"      : "Validation failed: session1Id: SessionResource sessionId cannot be null, and type: SessionResource type cannot be null"
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
        @DisplayName("GET /sessionResources -> returns all sessionResources")
        void getAll() throws Exception {

            SessionResource sessionResource1 = sessionResourceRepository.save(SessionResource.builder()
                    .session1Id(session1.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            SessionResource sessionResource2 = sessionResourceRepository.save(SessionResource.builder()
                    .session1Id(session1.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
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
                        "session1Id"     : %d,
                        "type"           : "%s",
                        "url"            :"desc"
                    },
                    {
                        "id"             : %d,
                        "session1Id"     : %d,
                        "type"           : "%s",
                        "url"            :"desc"
                    }
                ]
                """.formatted(sessionResource1.getId(), session1.getId(), resourceTypeDictionary.getId(), sessionResource2.getId(), session1.getId(), resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /sessionResources/{id} -> returns sessionResource by ID")
        void getById() throws Exception {

            SessionResource sessionResource = sessionResourceRepository.save(SessionResource.builder()
                    .session1Id(session1.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + sessionResource.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"             : %d,
                    "session1Id"     : %d,
                    "type"           : "%s",
                    "url"            :"desc"
                }
                """.formatted(sessionResource.getId(), session1.getId() ,resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /sessionResources/{id} with unknown ID -> returns 404")
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
                    "message": "SessionResource not found by id: 999999"
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
        @DisplayName("PUT /sessionResources -> updates and returns the SessionResource")
        void update() throws Exception {
            SessionResource original = sessionResourceRepository.save(SessionResource.builder()
                    .session1Id(session1.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String updateRequest = """
                {
                    "id"             : %d,
                    "session1Id"     : %d,
                    "type"           : "%s",
                    "url"            : "descUPD"
                }
                """.formatted(original.getId(), session1.getId(), resourceTypeDictionary.getId());

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
                    "session1Id"     : %d,
                    "type"           : "%s",
                    "url"            :"descUPD"
                }
                """.formatted(original.getId(), session1.getId(), resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            SessionResource updated = sessionResourceRepository.findById(original.getId()).orElseThrow();
            assertEquals("descUPD", updated.getUrl());
        }

        @Test
        @DisplayName("PUT /sessionResources with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"             : 999999,
                    "session1Id"     : %d,
                    "type"           : "%s",
                    "url"            :"desc"
                }
                """.formatted(session1.getId(), resourceTypeDictionary.getId());

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
                    "message": "SessionResourceService. Could not update SessionResource by id: 999999"
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
        @DisplayName("DELETE /sessionResources/{id} -> deletes the SessionResource")
        void delete() throws Exception {
            SessionResource sessionResource = sessionResourceRepository.save(SessionResource.builder()
                    .session1Id(session1.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + sessionResource.getId())
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

            assertFalse(sessionResourceRepository.existsById(sessionResource.getId()));
        }

        @Test
        @DisplayName("DELETE /sessionResources/{id} with unknown ID -> returns 404")
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
                    "message": "SessionResourceService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}