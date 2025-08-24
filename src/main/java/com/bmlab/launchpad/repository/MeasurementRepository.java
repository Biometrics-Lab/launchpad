package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Measurement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends CrudRepository<Measurement, Integer> {
}
