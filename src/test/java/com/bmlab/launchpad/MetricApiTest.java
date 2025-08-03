package com.bmlab.launchpad;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MetricApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreate() throws Exception {
        String json = """
            {
                "name": "Test Metric",
                "measurementId": 123,
                "negate": false
            }
        """;

        mockMvc.perform(post("http://localhost:8080/api/v1/metrics")
                        .content(json)
                        .with(httpBasic("biolab", "biolab"))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Metric"))
                .andExpect(jsonPath("$.measurementId").value(123))
                .andExpect(jsonPath("$.negate").value(false));
    }
}