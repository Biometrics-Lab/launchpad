package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.ResourceTypeDictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceTypeDictionaryRepository extends CrudRepository<ResourceTypeDictionary, String> {
}
