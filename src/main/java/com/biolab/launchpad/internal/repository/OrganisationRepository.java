package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Organisation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends CrudRepository<Organisation, Integer> {
}
