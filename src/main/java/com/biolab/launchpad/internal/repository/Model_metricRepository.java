package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Model_metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Model_metricRepository extends CrudRepository<Model_metric, Integer> {
}
