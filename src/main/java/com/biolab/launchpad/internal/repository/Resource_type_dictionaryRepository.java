package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Resource_type_dictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Resource_type_dictionaryRepository extends CrudRepository<Resource_type_dictionary, String> {
}
