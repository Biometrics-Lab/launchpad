package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Age_group_dictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Age_group_dictionaryRepository extends CrudRepository<Age_group_dictionary, String> {
}
