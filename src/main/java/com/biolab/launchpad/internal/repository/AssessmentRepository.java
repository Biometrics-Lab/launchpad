package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Assessment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends CrudRepository<Assessment, Integer> {
}
