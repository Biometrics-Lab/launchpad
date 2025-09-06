package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Rep_metric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Rep_metricRepository extends CrudRepository<Rep_metric, Integer> {
}
