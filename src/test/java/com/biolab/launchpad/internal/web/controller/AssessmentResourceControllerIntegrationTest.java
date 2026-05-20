package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.AssessmentResourceRepository;
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
@DisplayName("AssessmentResource Integration Tests")
class AssessmentResourceControllerIntegrationTest {

    private static final String API = "/api/v1/assessmentResources";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AssessmentResourceRepository assessmentResourceRepository;

    @Autowired
    EntityFactory factory;

    Assessment  assessment;
    ResourceTypeDictionary resourceTypeDictionary;

    @BeforeEach
    void setUp() {
        assessment              = factory.createAssessment();
        resourceTypeDictionary  = factory.createResourceTypeDictionary("rTypeDictionary");
    }

    @AfterEach
    void tearDown() {
        assessmentResourceRepository.deleteAll();
        factory.cleanup();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /assessmentResources -> creates and returns the new AssessmentResource")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "assessmentId"   : %d,
                                    "type"           : "%s",
                                    "url"            : "desc",
                                    "urlStatus"      : "PENDING"
                                }
                            """.formatted(assessment.getId(), resourceTypeDictionary.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int assessmentResourceId = responseNode.get("id").asInt();
            assertThat(assessmentResourceId).isPositive();


            String expectedResponse =
                    """
                            {
                                        "id"             : %d,
                                        "assessmentId"   : %d,
                                        "type"           : "%s",
                                        "url"            : "desc",
                                        "externalUrl"    : null,
                                        "urlStatus"      : "PENDING"
                                    }
                            """.formatted(assessmentResourceId, assessment.getId(), resourceTypeDictionary.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /assessmentResources with validation message -> returns 422")
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
                                "message"      : "Validation failed: assessmentId: AssessmentResource assessmentId cannot be null, and type: AssessmentResource type cannot be null"
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
        @DisplayName("GET /assessmentResources -> returns all assessmentResources")
        void getAll() throws Exception {

            AssessmentResource assessmentResource1 = assessmentResourceRepository.save(AssessmentResource.builder()
                    .assessmentId(assessment.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            AssessmentResource assessmentResource2 = assessmentResourceRepository.save(AssessmentResource.builder()
                    .assessmentId(assessment.getId())
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
                        "assessmentId"   : %d,
                        "type"           : "%s",
                        "url"            : "desc",
                        "externalUrl"    : null,
                        "urlStatus"      : "PENDING"
                    },
                    {
                        "id"             : %d,
                        "assessmentId"   : %d,
                        "type"           : "%s",
                        "url"            : "desc",
                        "externalUrl"    : null,
                        "urlStatus"      : "PENDING"
                    }
                ]
                """.formatted(assessmentResource1.getId(), assessment.getId(), resourceTypeDictionary.getId(), assessmentResource2.getId(), assessment.getId(), resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessmentResources/{id} -> returns assessmentResource by ID")
        void getById() throws Exception {

            AssessmentResource assessmentResource = assessmentResourceRepository.save(AssessmentResource.builder()
                    .assessmentId(assessment.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + assessmentResource.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"             : %d,
                    "assessmentId"   : %d,
                    "type"           : "%s",
                    "url"            : "desc",
                    "externalUrl"    : null,
                    "urlStatus"      : "PENDING"
                }
                """.formatted(assessmentResource.getId(), assessment.getId() ,resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /assessmentResources/{id} with unknown ID -> returns 404")
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
                    "message": "AssessmentResource not found by id: 999999"
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
        @DisplayName("PUT /assessmentResources -> updates and returns the AssessmentResource")
        void update() throws Exception {
            AssessmentResource original = assessmentResourceRepository.save(AssessmentResource.builder()
                    .assessmentId(assessment.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String updateRequest = """
                {
                    "id"             : %d,
                    "assessmentId"   : %d,
                    "type"           : "%s",
                    "url"            : "descUPD",
                    "urlStatus"      : "PENDING"
                }
                """.formatted(original.getId(), assessment.getId(), resourceTypeDictionary.getId());

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
                    "assessmentId"   : %d,
                    "type"           : "%s",
                    "url"            : "descUPD",
                    "externalUrl"    : null,
                    "urlStatus"      : "PENDING"
                }
                """.formatted(original.getId(), assessment.getId(), resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            AssessmentResource updated = assessmentResourceRepository.findById(original.getId()).orElseThrow();
            assertEquals("descUPD", updated.getUrl());
        }

        @Test
        @DisplayName("PUT /assessmentResources with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"             : 999999,
                    "assessmentId"   : %d,
                    "type"           : "%s",
                    "url"            :"desc"
                }
                """.formatted(assessment.getId(), resourceTypeDictionary.getId());

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
                    "message": "AssessmentResourceService. Could not update AssessmentResource by id: 999999"
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
        @DisplayName("DELETE /assessmentResources/{id} -> deletes the AssessmentResource")
        void delete() throws Exception {
            AssessmentResource assessmentResource = assessmentResourceRepository.save(AssessmentResource.builder()
                    .assessmentId(assessment.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + assessmentResource.getId())
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

            assertFalse(assessmentResourceRepository.existsById(assessmentResource.getId()));
        }

        @Test
        @DisplayName("DELETE /assessmentResources/{id} with unknown ID -> returns 404")
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
                    "message": "AssessmentResourceService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}