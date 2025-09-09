package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.User_role_dictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_role_dictionaryRepository extends CrudRepository<User_role_dictionary, String> {
}
