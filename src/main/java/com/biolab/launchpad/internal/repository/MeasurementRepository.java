package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Measurement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends CrudRepository<Measurement, Integer> {
}
