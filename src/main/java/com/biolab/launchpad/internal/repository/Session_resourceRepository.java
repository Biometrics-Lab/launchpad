package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Session_resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Session_resourceRepository extends CrudRepository<Session_resource, Integer> {
}
