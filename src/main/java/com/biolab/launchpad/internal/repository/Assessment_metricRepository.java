package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Assessment_metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Assessment_metricRepository extends CrudRepository<Assessment_metric, Integer> {
}
