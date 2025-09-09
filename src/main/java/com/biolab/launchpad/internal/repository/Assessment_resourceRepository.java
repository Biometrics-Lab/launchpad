package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Assessment_resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Assessment_resourceRepository extends CrudRepository<Assessment_resource, Integer> {
}
