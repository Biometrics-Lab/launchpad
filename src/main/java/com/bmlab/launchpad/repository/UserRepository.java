package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
}
