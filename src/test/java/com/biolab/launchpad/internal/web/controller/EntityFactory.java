package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityFactory {

    private final MeasurementRepository              measurementRepository;
    private final MetricRepository                   metricRepository;
    private final AgeGroupDictionaryRepository       ageGroupDictionaryRepository;
    private final DataSourceTypeDictionaryRepository dataSourceTypeDictionaryRepository;
    private final ResourceTypeDictionaryRepository   resourceTypeDictionaryRepository;
    private final SportDictionaryRepository          sportDictionaryRepository;
    private final UserRoleDictionaryRepository       userRoleDictionaryRepository;

    public EntityFactory(MeasurementRepository measurementRepository, MetricRepository metricRepository, AgeGroupDictionaryRepository ageGroupDictionaryRepository, DataSourceTypeDictionaryRepository dataSourceTypeDictionaryRepository, ResourceTypeDictionaryRepository resourceTypeDictionaryRepository, SportDictionaryRepository sportDictionaryRepository, UserRoleDictionaryRepository userRoleDictionaryRepository){
        this.measurementRepository              = measurementRepository;
        this.metricRepository                   = metricRepository;
        this.ageGroupDictionaryRepository       = ageGroupDictionaryRepository;
        this.dataSourceTypeDictionaryRepository = dataSourceTypeDictionaryRepository;
        this.resourceTypeDictionaryRepository   = resourceTypeDictionaryRepository;
        this.sportDictionaryRepository          = sportDictionaryRepository;
        this.userRoleDictionaryRepository       = userRoleDictionaryRepository;
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

    public AgeGroupDictionary createAgeGroupDictionary(String name) {

        Optional<AgeGroupDictionary> ageGroupDictionaryOptional = ageGroupDictionaryRepository.findById(name);
        if (ageGroupDictionaryOptional.isPresent()) {
            return ageGroupDictionaryOptional.get();
        }

        AgeGroupDictionary ageGroupDictionary = AgeGroupDictionary.builder()
                .name(name)
                .build();

        ageGroupDictionary.markAsNew(true);

        return ageGroupDictionaryRepository.save(ageGroupDictionary);

    }

    public DataSourceTypeDictionary createDataSourceTypeDictionary(String name) {

        Optional<DataSourceTypeDictionary> dataSourceTypeDictionaryOptional = dataSourceTypeDictionaryRepository.findById(name);
        if (dataSourceTypeDictionaryOptional.isPresent()) {
            return dataSourceTypeDictionaryOptional.get();
        }

        DataSourceTypeDictionary dataSourceTypeDictionary = DataSourceTypeDictionary.builder()
                .name(name)
                .build();

        dataSourceTypeDictionary.markAsNew(true);

        return dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary);

    }

    public ResourceTypeDictionary createResourceTypeDictionary(String name) {

        Optional<ResourceTypeDictionary> resourceTypeDictionaryOptional = resourceTypeDictionaryRepository.findById(name);
        if (resourceTypeDictionaryOptional.isPresent()) {
            return resourceTypeDictionaryOptional.get();
        }

        ResourceTypeDictionary resourceTypeDictionary = ResourceTypeDictionary.builder()
                .name(name)
                .build();

        resourceTypeDictionary.markAsNew(true);

        return resourceTypeDictionaryRepository.save(resourceTypeDictionary);

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

    public UserRoleDictionary createUserRoleDictionary(String name) {

        Optional<UserRoleDictionary> userRoleDictionaryOptional = userRoleDictionaryRepository.findById(name);
        if (userRoleDictionaryOptional.isPresent()) {
            return userRoleDictionaryOptional.get();
        }

        UserRoleDictionary userRoleDictionary = UserRoleDictionary.builder()
                .name(name)
                .build();

        userRoleDictionary.markAsNew(true);

        return userRoleDictionaryRepository.save(userRoleDictionary);

    }

    @AfterEach
    void cleanup() {
        metricRepository.deleteAll();
        measurementRepository.deleteAll();
        ageGroupDictionaryRepository.deleteAll();
        dataSourceTypeDictionaryRepository.deleteAll();
        resourceTypeDictionaryRepository.deleteAll();
        sportDictionaryRepository.deleteAll();
        userRoleDictionaryRepository.deleteAll();
    }

}
