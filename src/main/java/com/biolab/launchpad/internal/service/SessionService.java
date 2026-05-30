package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.Session;
import com.biolab.launchpad.internal.security.exceptions.BadRequestException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class SessionService extends EntityServiceID<Session> {

    private final AssessmentMetricRepository assessmentMetricRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository,
                          AssessmentMetricRepository assessmentMetricRepository) {
        super(sessionRepository);
        this.assessmentMetricRepository = assessmentMetricRepository;
    }

    @Override
    public Session create(Session session) {
        if (assessmentMetricRepository.findAllByAssessmentId(session.getAssessmentId()).isEmpty()) {
            throw new BadRequestException(
                "Cannot start session — assessment %d has no metrics".formatted(session.getAssessmentId())
            );
        }
        return super.create(session);
    }
}
