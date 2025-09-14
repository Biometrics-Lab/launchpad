package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.DataSourceTypeDictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataSourceTypeDictionaryRepository extends CrudRepository<DataSourceTypeDictionary, String> {
}
