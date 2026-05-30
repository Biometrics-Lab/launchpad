package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import com.biolab.launchpad.internal.security.exceptions.ConflictException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AssessmentMetricService extends EntityServiceID<AssessmentMetric> {

    private final AssessmentMetricRepository assessmentMetricRepository;
    private final SessionRepository sessionRepository;

    @Autowired
    public AssessmentMetricService(AssessmentMetricRepository assessmentMetricRepository,
                                   SessionRepository sessionRepository) {
        super(assessmentMetricRepository);
        this.assessmentMetricRepository = assessmentMetricRepository;
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void deleteById(Integer id) {
        assessmentMetricRepository.findById(id).ifPresent(metric -> {
            if (sessionRepository.countByAssessmentId(metric.getAssessmentId()) > 0) {
                throw new ConflictException(
                    "Cannot delete assessment metric — assessment %d has sessions".formatted(metric.getAssessmentId())
                );
            }
        });
        super.deleteById(id);
    }

    @Override
    public AssessmentMetric update(AssessmentMetric metric) {
        if (sessionRepository.countByAssessmentId(metric.getAssessmentId()) > 0) {
            throw new ConflictException(
                "Cannot update assessment metric — assessment %d has sessions".formatted(metric.getAssessmentId())
            );
        }
        return super.update(metric);
    }
}
