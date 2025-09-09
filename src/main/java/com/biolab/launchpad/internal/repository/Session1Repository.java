package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Session1;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Session1Repository extends CrudRepository<Session1, Integer> {
}
