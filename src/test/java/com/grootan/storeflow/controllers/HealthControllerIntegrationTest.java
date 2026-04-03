package com.grootan.storeflow.controllers;

import com.grootan.storeflow.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HealthControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void getHealth_returnsOkWithCorrectShape() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.jvmUptimeMs").isNumber());
    }

    @Test
    @org.springframework.security.test.context.support.WithMockUser
    void getUnknownRoute_returns404() throws Exception {
        mockMvc.perform(get("/api/nonexistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/nonexistent"));
    }

    @Test
    void getHealth_includesTraceIdHeader() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Trace-Id"));
    }


    @Test
    void getActuatorHealth_returnsOk() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
