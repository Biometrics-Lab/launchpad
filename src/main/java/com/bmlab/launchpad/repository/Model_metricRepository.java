package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Model_metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Model_metricRepository extends CrudRepository<Model_metric, Integer> {
}
