package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Rep_resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Rep_resourceRepository extends CrudRepository<Rep_resource, Integer> {
}
