package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentMetricRepository extends CrudRepository<AssessmentMetric, Integer> {
    List<AssessmentMetric> findAllByAssessmentId(Integer assessmentId);
    Optional<AssessmentMetric> findByAssessmentIdAndConditionalMetricId(Integer assessmentId, Integer conditionalMetricId);
}
