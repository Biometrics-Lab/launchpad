package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Session_resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Session_resourceRepository extends CrudRepository<Session_resource, Integer> {
}
