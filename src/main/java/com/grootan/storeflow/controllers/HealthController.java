package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.response.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.time.Instant;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        HealthResponse response = HealthResponse.builder()
                .status("UP")
                .timestamp(Instant.now())
                .jvmUptimeMs(uptime)
                .build();
        return ResponseEntity.ok(response);
    }
}
