package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Session;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("assessmentSessionRepository")
public interface SessionRepository extends CrudRepository<Session, Integer> {
}
