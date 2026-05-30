package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.TemplateMetricRepository;
import com.biolab.launchpad.internal.repository.model.Assessment;
import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AssessmentService extends EntityServiceID<Assessment> {

    private final TemplateMetricRepository templateMetricRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;

    @Autowired
    public AssessmentService(CrudRepository<Assessment, Integer> repository,
                             TemplateMetricRepository templateMetricRepository,
                             AssessmentMetricRepository assessmentMetricRepository) {
        super(repository);
        this.templateMetricRepository = templateMetricRepository;
        this.assessmentMetricRepository = assessmentMetricRepository;
    }

    @Override
    public Assessment create(Assessment assessment) {
        Assessment saved = super.create(assessment);
        templateMetricRepository.findByTemplateId(assessment.getTemplateId()).forEach(tm -> {
            AssessmentMetric am = AssessmentMetric.builder()
                    .assessmentId(saved.getId())
                    .conditionalMetricId(tm.getConditionalMetricId())
                    .dataSourceId(tm.getDataSourceId())
                    .description(tm.getDescription())
                    .build();
            assessmentMetricRepository.save(am);
        });
        return saved;
    }
}
