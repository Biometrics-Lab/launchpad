package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.RepResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepResourceRepository extends CrudRepository<RepResource, Integer> {
}
