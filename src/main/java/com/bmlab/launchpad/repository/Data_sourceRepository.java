package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Data_source;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Data_sourceRepository extends CrudRepository<Data_source, Integer> {
}
