package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends CrudRepository<Session, Integer> {
}
