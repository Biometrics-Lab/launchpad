package com.bmlab.launchpad.controller;

import com.bmlab.launchpad.repository.Metric;
import com.bmlab.launchpad.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/api/metrics")
public class MetricController {

    private final MetricService metricService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Metric> create(@RequestBody Metric metric) {
        if (metric.getId() != null) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request, ID should not be in the query
        }

        Metric created = metricService.create(metric); // create
        return ResponseEntity.status(HttpStatus.CREATED).body(created); // 201 Created
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (metricService.findById(id).isPresent()) {
            metricService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
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

