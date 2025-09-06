package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Session_metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Session_metricRepository extends CrudRepository<Session_metric, Integer> {
}
