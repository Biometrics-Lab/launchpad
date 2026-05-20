package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.AssessmentResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentResourceRepository extends CrudRepository<AssessmentResource, Integer> {
    Optional<AssessmentResource> findByUuid(UUID uuid);
}
