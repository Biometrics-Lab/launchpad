package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.SessionResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionResourceRepository extends CrudRepository<SessionResource, Integer> {
    Optional<SessionResource> findByUuid(UUID uuid);
}
