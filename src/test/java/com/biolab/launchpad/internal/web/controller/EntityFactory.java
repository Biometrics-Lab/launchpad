package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.MeasurementRepository;
import com.biolab.launchpad.internal.repository.MetricRepository;
import com.biolab.launchpad.internal.repository.SportDictionaryRepository;
import com.biolab.launchpad.internal.repository.model.AgeGroupDictionary;
import com.biolab.launchpad.internal.repository.model.Measurement;
import com.biolab.launchpad.internal.repository.model.Metric;
import com.biolab.launchpad.internal.repository.model.SportDictionary;
import com.biolab.launchpad.internal.web.dto.SportDictionaryDto;
import org.junit.jupiter.api.AfterEach;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.biolab.launchpad.internal.web.mapper.SportDictionaryMapper.sportDictionaryMapper;

@Component
public class EntityFactory {

    private final MeasurementRepository measurementRepository;
    private final MetricRepository metricRepository;
    private final SportDictionaryRepository sportDictionaryRepository;

    public EntityFactory(MeasurementRepository measurementRepository,
                         MetricRepository metricRepository,
                         SportDictionaryRepository sportDictionaryRepository) {
        this.measurementRepository     = measurementRepository;
        this.metricRepository          = metricRepository;
        this.sportDictionaryRepository = sportDictionaryRepository;
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

    public SportDictionary createSportDictionary(String name) {

        Optional<SportDictionary> sportDictionaryOptional = sportDictionaryRepository.findById(name);
        if (sportDictionaryOptional.isPresent()) {
            return sportDictionaryOptional.get();
        }

        SportDictionary sportDictionary = SportDictionary.builder()
                .name(name)
                .build();

        sportDictionary.markAsNew(true);

        return sportDictionaryRepository.save(sportDictionary);

    }

    @AfterEach
    void cleanup() {
        metricRepository.deleteAll();
        measurementRepository.deleteAll();
        sportDictionaryRepository.deleteAll();
    }

}
