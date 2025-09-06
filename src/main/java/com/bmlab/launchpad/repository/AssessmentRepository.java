package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Assessment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends CrudRepository<Assessment, Integer> {
}
