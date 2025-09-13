package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.SessionResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionResourceRepository extends CrudRepository<SessionResource, Integer> {
}
