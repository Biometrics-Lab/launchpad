package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.TemplateMetric;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateMetricRepository extends CrudRepository<TemplateMetric, Integer> {
    List<TemplateMetric> findByTemplateId(Integer templateId);
}
