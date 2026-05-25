package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.service.PlayerMetricsService;
import com.biolab.launchpad.internal.web.dto.PlayerMetricsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/players/{id}/metrics")
public class PlayerMetricsController {

    private final PlayerMetricsService playerMetricsService;

    @GetMapping
    public PlayerMetricsDto getMetrics(@PathVariable Integer id) {
        return playerMetricsService.getMetrics(id);
    }
}
