package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentMetricRepository extends CrudRepository<AssessmentMetric, Integer> {
    List<AssessmentMetric> findAllByAssessmentId(Integer assessmentId);
}
