package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.RepMetric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class RepMetricService extends EntityServiceID<RepMetric> {

    private final RepRepository repRepository;
    private final SessionRepository sessionRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;

    @Autowired
    public RepMetricService(RepMetricRepository repMetricRepository,
                            RepRepository repRepository,
                            SessionRepository sessionRepository,
                            AssessmentMetricRepository assessmentMetricRepository) {
        super(repMetricRepository);
        this.repRepository = repRepository;
        this.sessionRepository = sessionRepository;
        this.assessmentMetricRepository = assessmentMetricRepository;
    }

    @Override
    public RepMetric create(RepMetric repMetric) {
        RepMetric saved = super.create(repMetric);
        repRepository.findById(repMetric.getRepId()).ifPresent(rep ->
            sessionRepository.findById(rep.getSessionId()).ifPresent(session ->
                assessmentMetricRepository
                    .findByAssessmentIdAndConditionalMetricId(session.getAssessmentId(), repMetric.getConditionalMetricId())
                    .ifPresent(am -> {
                        am.setRepCount(am.getRepCount() + 1);
                        assessmentMetricRepository.save(am);
                    })
            )
        );
        return saved;
    }
}
