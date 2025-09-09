package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Template_metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Template_metricRepository extends CrudRepository<Template_metric, Integer> {
}
