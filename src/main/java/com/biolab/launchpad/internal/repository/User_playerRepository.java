package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.User_player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface User_playerRepository extends CrudRepository<User_player, Integer> {
}
