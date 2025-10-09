package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.repository.model.ResourceTypeDictionary;
import com.biolab.launchpad.internal.repository.model.Session1;
import com.biolab.launchpad.internal.repository.model.RepResource;
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
@DisplayName("RepResource Integration Tests")
class RepResourceControllerIntegrationTest {

    private static final String API = "/api/v1/repResources";

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RepResourceRepository repResourceRepository;

    @Autowired
    EntityFactory factory;

    Rep rep;
    ResourceTypeDictionary resourceTypeDictionary;

    @BeforeEach
    void setUp() {
        rep                     = factory.createRep("Jay-Z");
        resourceTypeDictionary  = factory.createResourceTypeDictionary("rTypeDictionary");
    }

    @AfterEach
    void tearDown() {
        repResourceRepository.deleteAll();
    }

    @Nested
    @DisplayName("Create")
    class CreateTests {
        @Test
        @DisplayName("POST /repResource -> creates and returns the new RepResource")
        void create() throws Exception {

            String request =
                            """
                                {
                                    "repId"          : %d,
                                    "type"           : "%s",
                                    "url"            : "desc"
                                }
                            """.formatted(rep.getId(), resourceTypeDictionary.getId());

            String jsonResponse = mvc.perform(
                            post(API)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(request)
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();


            JsonNode responseNode = objectMapper.readTree(jsonResponse);

            int repResourceId = responseNode.get("id").asInt();
            assertThat(repResourceId).isPositive();


            String expectedResponse =
                    """
                            {
                                        "id"             : %d,
                                        "repId"          : %d,
                                        "type"           : "%s",
                                        "url"            :"desc"
                                    }
                            """.formatted(repResourceId, rep.getId(), resourceTypeDictionary.getId());

            JsonNode expectedResponseNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedResponseNode, responseNode);
        }

        @Test
        @DisplayName("POST /repResource with validation message -> returns 422")
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
                                "message"      : "Validation failed: repId: RepResource repId cannot be null, and type: RepResource type cannot be null"
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
        @DisplayName("GET /repResource -> returns all repResource")
        void getAll() throws Exception {

            RepResource repResource1 = repResourceRepository.save(RepResource.builder()
                    .repId(rep.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            RepResource repResource2 = repResourceRepository.save(RepResource.builder()
                    .repId(rep.getId())
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
                        "repId"          : %d,
                        "type"           : "%s",
                        "url"            :"desc"
                    },
                    {
                        "id"             : %d,
                        "repId"          : %d,
                        "type"           : "%s",
                        "url"            :"desc"
                    }
                ]
                """.formatted(repResource1.getId(), rep.getId(), resourceTypeDictionary.getId(), repResource2.getId(), rep.getId(), resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /repResource/{id} -> returns repResource by ID")
        void getById() throws Exception {

            RepResource repResource = repResourceRepository.save(RepResource.builder()
                    .repId(rep.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            get(API + "/" + repResource.getId())
                                    .with(httpBasic("biolab", "biolab"))
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            String expectedResponse = """
                {
                    "id"             : %d,
                    "repId"          : %d,
                    "type"           : "%s",
                    "url"            :"desc"
                }
                """.formatted(repResource.getId(), rep.getId() ,resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }

        @Test
        @DisplayName("GET /repResource/{id} with unknown ID -> returns 404")
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
                    "message": "RepResource not found by id: 999999"
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
        @DisplayName("PUT /repResource -> updates and returns the RepResource")
        void update() throws Exception {
            RepResource original = repResourceRepository.save(RepResource.builder()
                    .repId(rep.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String updateRequest = """
                {
                    "id"             : %d,
                    "repId"          : %d,
                    "type"           : "%s",
                    "url"            : "descUPD"
                }
                """.formatted(original.getId(), rep.getId(), resourceTypeDictionary.getId());

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
                    "repId"          : %d,
                    "type"           : "%s",
                    "url"            :"descUPD"
                }
                """.formatted(original.getId(), rep.getId(), resourceTypeDictionary.getId());

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);

            assertEquals(expectedNode, responseNode);

            RepResource updated = repResourceRepository.findById(original.getId()).orElseThrow();
            assertEquals("descUPD", updated.getUrl());
        }

        @Test
        @DisplayName("PUT /repResource with invalid id -> returns 404")
        void updateValidationError() throws Exception {

            String updateRequest = """
                {
                    "id"             : 999999,
                    "repId"          : %d,
                    "type"           : "%s",
                    "url"            :"desc"
                }
                """.formatted(rep.getId(), resourceTypeDictionary.getId());

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
                    "message": "RepResourceService. Could not update RepResource by id: 999999"
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
        @DisplayName("DELETE /repResource/{id} -> deletes the RepResource")
        void delete() throws Exception {
            RepResource repResource = repResourceRepository.save(RepResource.builder()
                    .repId(rep.getId())
                    .type(resourceTypeDictionary.getId())
                    .url("desc")
                    .build());

            String jsonResponse = mvc.perform(
                            MockMvcRequestBuilders.delete(API + "/" + repResource.getId())
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

            assertFalse(repResourceRepository.existsById(repResource.getId()));
        }

        @Test
        @DisplayName("DELETE /repResource/{id} with unknown ID -> returns 404")
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
                    "message": "RepResourceService. Could not delete id: 999999"
                }
                """;

            JsonNode expectedNode = objectMapper.readTree(expectedResponse);
            JsonNode actualNode   = objectMapper.readTree(jsonResponse);

            assertEquals(expectedNode, actualNode);
        }
    }

}