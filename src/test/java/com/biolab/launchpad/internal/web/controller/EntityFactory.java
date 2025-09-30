package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.MeasurementRepository;
import com.biolab.launchpad.internal.repository.MetricRepository;
import com.biolab.launchpad.internal.repository.model.Measurement;
import com.biolab.launchpad.internal.repository.model.Metric;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;

@Component
public class EntityFactory {

    private final MeasurementRepository measurementRepository;
    private final MetricRepository metricRepository;

    public EntityFactory(MeasurementRepository measurementRepository,
                         MetricRepository metricRepository) {
        this.measurementRepository = measurementRepository;
        this.metricRepository = metricRepository;
    }

    public Measurement createMeasurement(String name) {
        return measurementRepository.save(
                Measurement.builder()
                        .name(name)
                        .build()
        );
    }

    public Metric createMetric(String name) {
        Measurement measurement = createMeasurement("AutoMeasurement_" + name);
        return metricRepository.save(
                Metric.builder()
                        .name(name)
                        .measurementId(measurement.getId())
                        .build()
        );
    }

    @AfterEach
    void cleanup() {
        metricRepository.deleteAll();
        measurementRepository.deleteAll();
    }

}
