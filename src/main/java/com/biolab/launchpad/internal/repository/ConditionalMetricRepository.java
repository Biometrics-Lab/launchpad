package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ConditionalMetricRepository extends CrudRepository<ConditionalMetric, Integer> {
    List<ConditionalMetric> findAllByIdIn(Collection<Integer> ids);
}
