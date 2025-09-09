package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends CrudRepository<Model, Integer> {
}
