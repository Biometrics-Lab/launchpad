package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.RepMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepMetricRepository extends CrudRepository<RepMetric, Integer> {
}
