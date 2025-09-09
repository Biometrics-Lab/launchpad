package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Rep_metric_source;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Rep_metric_sourceRepository extends CrudRepository<Rep_metric_source, Integer> {
}
