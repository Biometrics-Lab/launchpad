package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.RepResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RepResourceRepository extends CrudRepository<RepResource, Integer> {
    Optional<RepResource> findByUuid(UUID uuid);
}
