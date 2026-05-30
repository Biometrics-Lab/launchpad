package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.SessionMetricRepository;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.SessionMetric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class SessionMetricService extends EntityServiceID<SessionMetric> {

    private final SessionRepository sessionRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;

    @Autowired
    public SessionMetricService(SessionMetricRepository sessionMetricRepository,
                                SessionRepository sessionRepository,
                                AssessmentMetricRepository assessmentMetricRepository) {
        super(sessionMetricRepository);
        this.sessionRepository = sessionRepository;
        this.assessmentMetricRepository = assessmentMetricRepository;
    }

    @Override
    public SessionMetric create(SessionMetric sessionMetric) {
        SessionMetric saved = super.create(sessionMetric);
        sessionRepository.findById(sessionMetric.getSessionId()).ifPresent(session ->
            assessmentMetricRepository
                .findByAssessmentIdAndConditionalMetricId(session.getAssessmentId(), sessionMetric.getConditionalMetricId())
                .ifPresent(am -> {
                    am.setSessionCount(am.getSessionCount() + 1);
                    assessmentMetricRepository.save(am);
                })
        );
        return saved;
    }
}
