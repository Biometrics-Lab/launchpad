package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.SessionMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionMetricRepository extends CrudRepository<SessionMetric, Integer> {
}
