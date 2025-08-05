package com.bmlab.launchpad.metric;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class metricControllerIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void Create() throws Exception {

         String jsonRequest = """
                            {
                                "name"         : "Test Create Metric",
                                "measurementId": null,
                                "negate"       : false
                            }
                            """;

        var response = mockMvc.perform(
                post("/api/v1/metrics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest)
                        .with(httpBasic("biolab", "biolab"))
                    )
                     .andReturn();

        String jsonResponse = response.getResponse().getContentAsString();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode nodeRequest  = mapper.readTree(jsonRequest);
        JsonNode nodeResponse = mapper.readTree(jsonResponse);

        assertEquals(nodeRequest.get("name")         .asText(), nodeResponse.get("name")         .asText());
        assertEquals(nodeRequest.get("measurementId").asText(), nodeResponse.get("measurementId").asText());
        assertEquals(nodeRequest.get("negate")       .asText(), nodeResponse.get("negate")       .asText());

    }
}