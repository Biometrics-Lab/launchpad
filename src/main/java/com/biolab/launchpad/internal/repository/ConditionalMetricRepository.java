package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionalMetricRepository extends CrudRepository<ConditionalMetric, Integer> {
}
