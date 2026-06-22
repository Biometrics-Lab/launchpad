package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.repository.model.Session;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Rep Integration Tests")
class RepControllerIntegrationTest {

    private static final String API = "/api/v1/reps";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RepRepository repRepository;

    @Autowired
    EntityFactory factory;

    Session session;

    @BeforeEach
    void setUp() {
        session = factory.createSession();
    }

    @AfterEach
    void tearDown() {
        repRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /reps -> creates and returns the new Rep")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "sessionId":  %d,
                                    "startTime" : "2025-10-02T14:45:00.000+00:00"
                                }
                            """.formatted(session.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int repId = responseNode.get("id").asInt();
            assertThat(repId).isPositive();


            String expectedResponse =
                                    """
                                      {
                                        "id"        : %d,
                                        "sessionId":  %d,
                                        "startTime" : "2025-10-02T14:45:00.000+00:00"
                                      }
                                    """.formatted(repId, session.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /reps with validation message -> returns 422")
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
                                "message"      : "Validation failed: sessionId: Rep sessionId cannot be null, and startTime: Rep startTime cannot be null"
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
        @DisplayName("GET /reps -> returns all reps")
        void getAll() throws Exception {

            ZonedDateTime zdt = ZonedDateTime.of(2025, 10, 2, 15, 45, 10, 0, ZoneOffset.UTC);

            Rep rep1 = repRepository.save(Rep.builder()
                    .sessionId(session.getId())
                    .startTime(Timestamp.from(zdt.toInstant()))
                    .build());

            Rep rep2 = repRepository.save(Rep.builder()
                    .sessionId(session.getId())
                    .startTime(Timestamp.from(zdt.toInstant()))
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
                        "sessionId" : %d,
                        "startTime"  : "2025-10-02T15:45:10.000+00:00"
                    },
                    {
                        "id"          : %d,
                        "sessionId"  : %d,
                        "startTime"   : "2025-10-02T15:45:10.000+00:00"
                    }
                ]
                """.formatted(rep1.getId(), session.getId(), rep2.getId(), session.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /reps/{id} -> returns rep by ID")
        void getById() throws Exception {

            ZonedDateTime zdt = ZonedDateTime.of(2025, 10, 2, 15, 45, 10, 0, ZoneOffset.UTC);

            Rep rep = repRepository.save(Rep.builder()
                    .sessionId(session.getId())
                    .startTime(Timestamp.from(zdt.toInstant()))
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + rep.getId())
                            .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                                     {
                                        "id"           : %d,
                                         "sessionId"  : %d,
                                         "startTime"   : "2025-10-02T15:45:10.000+00:00"
                                     }
                                    """.formatted(rep.getId(), session.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /reps/{id} with unknown ID -> returns 404")
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
                    "message": "Rep not found by id: 999999"
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
        @DisplayName("PUT /reps -> updates and returns the Rep")
        void update() throws Exception {
            Rep original = repRepository.save(Rep.builder()
                    .sessionId(session.getId())
                    .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                    .build());

            String updateRequest = """
                                {
                                    "id"         : %d,
                                    "sessionId" : %d,
                                    "startTime"  : "2026-10-02T14:45:00.000+00:00"
                                 }
                                """.formatted(original.getId(), session.getId());

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
                                    "id"         : %d,
                                    "sessionId" : %d,
                                    "startTime"  : "2026-10-02T14:45:00.000+00:00"
                                 }
                                """.formatted(original.getId(), session.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);
        }

        @Test
        @DisplayName("PUT /reps with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                                 {
                                     "id"         : 999999,
                                     "sessionId" : %d,
                                     "startTime"  : "2025-10-02T14:45:00.000+00:00"
                                 }
                                 """.formatted(session.getId());

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
                    "message": "RepService. Could not update Rep by id: 999999"
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
        @DisplayName("DELETE /reps/{id} -> deletes the Rep")
        void delete() throws Exception {
            Rep rep = repRepository.save(Rep.builder()
                    .sessionId(session.getId())
                    .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + rep.getId())
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

            assertFalse(repRepository.existsById(rep.getId()));
        }

        @Test
        @DisplayName("DELETE /reps/{id} with unknown ID -> returns 404")
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
                    "message": "RepService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

    @Nested
    @DisplayName("Get Resources")
    class GetResourcesTests {

        @Autowired
        com.biolab.launchpad.internal.repository.RepResourceRepository repResourceRepository;

        @AfterEach
        void tearDownResources() {
            repResourceRepository.deleteAll();
        }

        @Test
        @DisplayName("GET /reps/{id}/resources -> returns empty list when rep has no resources")
        void getResources_empty() throws Exception {
            com.biolab.launchpad.internal.repository.model.Rep rep = repRepository.save(
                    com.biolab.launchpad.internal.repository.model.Rep.builder()
                            .sessionId(session.getId())
                            .startTime(java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                            .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + rep.getId() + "/resources")
                                    .with(httpBasic("biolab", "biolab")))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode actualNode = objectMapper.readTree(jsonResponse);
            assertTrue(actualNode.isArray());
            assertEquals(0, actualNode.size());
        }

        @Test
        @DisplayName("GET /reps/{id}/resources -> returns resources with urlStatus")
        void getResources_withResources() throws Exception {
            factory.createResourceTypeDictionary("Video");

            com.biolab.launchpad.internal.repository.model.Rep rep = repRepository.save(
                    com.biolab.launchpad.internal.repository.model.Rep.builder()
                            .sessionId(session.getId())
                            .startTime(java.sql.Timestamp.valueOf(java.time.LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                            .build());

            com.biolab.launchpad.internal.repository.model.RepResource resource = repResourceRepository.save(
                    com.biolab.launchpad.internal.repository.model.RepResource.builder()
                            .repId(rep.getId())
                            .type("Video")
                            .url("http://localhost:4566/biolab-resources/test/cam1.mp4")
                            .urlStatus(com.biolab.common.UrlStatus.READY)
                            .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + rep.getId() + "/resources")
                                    .with(httpBasic("biolab", "biolab")))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            JsonNode actualNode = objectMapper.readTree(jsonResponse);
            assertTrue(actualNode.isArray());
            assertEquals(1, actualNode.size());

            JsonNode r = actualNode.get(0);
            assertEquals(resource.getId().intValue(), r.get("id").asInt());
            assertEquals(rep.getId().intValue(), r.get("repId").asInt());
            assertEquals("Video", r.get("type").asText());
            assertEquals("READY", r.get("urlStatus").asText());
        }
    }

}