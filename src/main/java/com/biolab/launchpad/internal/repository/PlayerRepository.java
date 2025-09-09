package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {
}
