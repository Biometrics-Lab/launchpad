package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentMetricRepository extends CrudRepository<AssessmentMetric, Integer> {
}
