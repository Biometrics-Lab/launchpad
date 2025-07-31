package com.bmlab.launchpad.controller;

import com.bmlab.launchpad.dto.MetricDTO;
import com.bmlab.launchpad.repository.Metric;
import com.bmlab.launchpad.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics")
public class MetricController {

    private final MetricService metricService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MetricDTO metricDTO) {

        if (metricDTO.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Name must not be null");
        }

        Metric created = metricService.create(metricDTO);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to create metric");
        }
        return ResponseEntity.ok(created);
    }


    @GetMapping
    public Iterable<Metric> getAll() {
        return metricService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Metric> getById(@PathVariable Integer id) {
        return metricService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (metricService.findById(id).isPresent()) {
            metricService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Metric> update(@PathVariable Integer id, @RequestBody Metric updatedMetric) {

        if (metricService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        updatedMetric.setId(id);
        Metric saved = metricService.update(updatedMetric);
        return ResponseEntity.ok(saved); // 200 OK
    }


}

