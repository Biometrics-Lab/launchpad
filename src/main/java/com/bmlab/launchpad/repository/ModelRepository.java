package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Model;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends CrudRepository<Model, Integer> {
}
