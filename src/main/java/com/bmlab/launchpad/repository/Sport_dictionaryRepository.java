package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Sport_dictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Sport_dictionaryRepository extends CrudRepository<Sport_dictionary, String> {
}
