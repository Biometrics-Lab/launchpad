package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.UserRoleDictionary;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleDictionaryRepository extends CrudRepository<UserRoleDictionary, String> {
}
