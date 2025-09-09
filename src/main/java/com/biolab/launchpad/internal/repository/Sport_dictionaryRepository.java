package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Sport_dictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Sport_dictionaryRepository extends CrudRepository<Sport_dictionary, String> {
}
