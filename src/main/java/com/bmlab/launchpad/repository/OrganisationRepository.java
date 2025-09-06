package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Organisation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends CrudRepository<Organisation, Integer> {
}
