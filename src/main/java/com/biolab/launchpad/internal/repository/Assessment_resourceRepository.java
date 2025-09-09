package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Assessment_resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Assessment_resourceRepository extends CrudRepository<Assessment_resource, Integer> {
}
