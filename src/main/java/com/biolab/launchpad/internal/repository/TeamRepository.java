package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends CrudRepository<Team, Integer> {
}
