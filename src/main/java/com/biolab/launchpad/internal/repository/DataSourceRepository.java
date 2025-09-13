package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.DataSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceRepository extends CrudRepository<DataSource, Integer> {
}
