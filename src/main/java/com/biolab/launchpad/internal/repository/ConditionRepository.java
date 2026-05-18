package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Condition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConditionRepository extends CrudRepository<Condition, Integer> {
}
