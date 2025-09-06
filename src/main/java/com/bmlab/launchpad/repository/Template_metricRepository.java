package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Template_metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Template_metricRepository extends CrudRepository<Template_metric, Integer> {
}
