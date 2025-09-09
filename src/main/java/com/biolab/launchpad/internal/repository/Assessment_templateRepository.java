package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Assessment_template;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Assessment_templateRepository extends CrudRepository<Assessment_template, Integer> {
}
