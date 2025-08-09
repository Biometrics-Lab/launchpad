package com.bmlab.launchpad.controller;

import com.bmlab.launchpad.dto.MetricDTO;
import com.bmlab.launchpad.repository.Metric;
import com.bmlab.launchpad.service.MetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/metrics")
public class MetricController {

    private final MetricService metricService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MetricDTO metricDTO) {

        if (metricDTO.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Name must not be null"); //400
        }

        Metric created = metricService.create(metricDTO);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Metric could not be created with provided data"); //422
        }

        MetricDTO createdDTO = convertToDTO(created);
        return ResponseEntity.ok(createdDTO);
    }

    @GetMapping
    public Iterable<MetricDTO> getAll() {
        return StreamSupport.stream(metricService.findAll().spliterator(), false)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<MetricDTO> getById(@PathVariable Integer id) {
        Optional<Metric> metricOptional = metricService.findById(id);
        return metricOptional
                .map(metric -> ResponseEntity.ok(convertToDTO(metric)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (metricService.findById(id).isPresent()) {
            metricService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody MetricDTO metricDTO) {

        if (metricDTO.getName() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Name must not be null"); // 400
        }

        Optional<Metric> metricOptional = metricService.findById(id);
        if (metricOptional.isEmpty()) {
            return ResponseEntity.notFound().build(); // 404
        }

        Metric updatedMetric = metricOptional.get();

        updatedMetric.setName         (metricDTO.getName());
        updatedMetric.setMeasurementId(metricDTO.getMeasurementId());
        updatedMetric.setNegate       (metricDTO.getNegate());

        Metric updated = metricService.update(updatedMetric);

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body("Metric could not be updated with provided data"); // 422
        }

        MetricDTO updatedDTO = convertToDTO(updated);
        return ResponseEntity.ok(updatedDTO);
    }

    private MetricDTO convertToDTO(Metric metric) {
        return MetricDTO.builder()
                .name(metric.getName())
                .measurementId(metric.getMeasurementId())
                .negate(metric.getNegate())
                .build();
    }

}

