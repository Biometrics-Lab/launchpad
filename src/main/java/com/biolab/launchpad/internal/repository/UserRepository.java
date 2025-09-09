package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
}
