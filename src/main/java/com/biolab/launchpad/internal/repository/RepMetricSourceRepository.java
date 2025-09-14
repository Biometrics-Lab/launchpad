package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.RepMetricSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepMetricSourceRepository extends CrudRepository<RepMetricSource, Integer> {
}
