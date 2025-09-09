package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Data_source;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Data_sourceRepository extends CrudRepository<Data_source, Integer> {
}
