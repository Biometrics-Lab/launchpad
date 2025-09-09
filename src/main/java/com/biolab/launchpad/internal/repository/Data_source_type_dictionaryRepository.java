package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Data_source_type_dictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Data_source_type_dictionaryRepository extends CrudRepository<Data_source_type_dictionary, String> {
}
