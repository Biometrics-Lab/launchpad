package com.bmlab.launchpad.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MetricRepository extends CrudRepository<Metric, Long> {


}
