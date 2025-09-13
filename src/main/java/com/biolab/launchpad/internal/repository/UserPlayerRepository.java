package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.UserPlayer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPlayerRepository extends CrudRepository<UserPlayer, Integer> {
}
