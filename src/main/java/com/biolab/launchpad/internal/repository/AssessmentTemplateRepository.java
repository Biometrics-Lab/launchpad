package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.AssessmentTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentTemplateRepository extends CrudRepository<AssessmentTemplate, Integer> {
}
