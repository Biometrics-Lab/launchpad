package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.SportDictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportDictionaryRepository extends CrudRepository<SportDictionary, String> {
}
