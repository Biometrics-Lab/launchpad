package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricRepository extends CrudRepository<Metric, Integer> {
}
