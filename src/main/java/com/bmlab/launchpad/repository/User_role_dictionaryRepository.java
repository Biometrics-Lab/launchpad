package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.User_role_dictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_role_dictionaryRepository extends CrudRepository<User_role_dictionary, String> {
}
