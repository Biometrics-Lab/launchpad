package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Integration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationRepository extends CrudRepository<Integration, Integer> {
}
