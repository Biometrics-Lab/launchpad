package com.biolab.launchpad.internal.service;

import com.biolab.common.SessionMetricConfig;
import com.biolab.common.SessionStartedEvent;
import com.biolab.common.SessionStoppedEvent;
import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import com.biolab.launchpad.internal.security.exceptions.ConflictException;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.web.dto.SessionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.biolab.launchpad.internal.web.mapper.SessionMapper.sessionMapper;

@Service
@RequiredArgsConstructor
@Log4j2
public class SessionWorkflowService {

    private final SessionRepository sessionRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;
    private final DataSourceRepository dataSourceRepository;
    private final RepRepository repRepository;
    private final RepMetricRepository repMetricRepository;
    private final SessionMetricRepository sessionMetricRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SessionDto startSession(Integer id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundByException("Session not found by id: %d", id));

        if (session.getStatus() != null) {
            throw new ConflictException("Session already started");
        }

        session.setStatus("ACTIVE");
        Session saved = sessionRepository.save(session);

        List<AssessmentMetric> assessmentMetrics =
                assessmentMetricRepository.findAllByAssessmentId(session.getAssessmentId());

        List<SessionMetricConfig> metricConfigs = assessmentMetrics.stream()
                .map(am -> {
                    DataSource ds = dataSourceRepository.findById(am.getDataSourceId())
                            .orElseThrow(() -> new NotFoundByException("DataSource not found by id: %d", am.getDataSourceId()));
                    return new SessionMetricConfig(
                            am.getConditionalMetricId(),
                            am.getDataSourceId(),
                            ds.getContent());
                })
                .toList();

        eventPublisher.publishEvent(new SessionStartedEvent(id, metricConfigs));
        log.info("Session {} started with {} metric configs", id, metricConfigs.size());

        return sessionMapper.toDto(saved);
    }

    @Transactional
    public SessionDto stopSession(Integer id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundByException("Session not found by id: %d", id));

        if (!"ACTIVE".equals(session.getStatus())) {
            throw new ConflictException("Session is not active");
        }

        session.setStatus("COMPLETE");
        Session saved = sessionRepository.save(session);

        computeAggregates(id, session.getAssessmentId());

        eventPublisher.publishEvent(new SessionStoppedEvent(id));
        log.info("Session {} stopped", id);

        return sessionMapper.toDto(saved);
    }

    private void computeAggregates(Integer sessionId, Integer assessmentId) {
        List<Rep> reps = repRepository.findAllBySessionId(sessionId);
        if (reps.isEmpty()) return;

        List<Integer> repIds = reps.stream().map(Rep::getId).toList();
        List<RepMetric> repMetrics = repMetricRepository.findAllByRepIdIn(repIds);

        Map<Integer, List<Number>> byMetric = repMetrics.stream()
                .collect(Collectors.groupingBy(
                        RepMetric::getConditionalMetricId,
                        Collectors.mapping(RepMetric::getValue, Collectors.toList())));

        byMetric.forEach((conditionalMetricId, values) -> {
            double min = values.stream().mapToDouble(Number::doubleValue).min().orElse(0);
            double max = values.stream().mapToDouble(Number::doubleValue).max().orElse(0);
            double avg = values.stream().mapToDouble(Number::doubleValue).average().orElse(0);

            sessionMetricRepository
                    .findBySessionIdAndConditionalMetricId(sessionId, conditionalMetricId)
                    .ifPresentOrElse(
                            existing -> {
                                existing.setMinValue(min);
                                existing.setMaxValue(max);
                                existing.setAvgValue(avg);
                                sessionMetricRepository.save(existing);
                            },
                            () -> sessionMetricRepository.save(SessionMetric.builder()
                                    .sessionId(sessionId)
                                    .conditionalMetricId(conditionalMetricId)
                                    .minValue(min)
                                    .maxValue(max)
                                    .avgValue(avg)
                                    .build())
                    );
        });

        updateAssessmentMetrics(sessionId, assessmentId);
    }

    private void updateAssessmentMetrics(Integer sessionId, Integer assessmentId) {
        List<Integer> allSessionIds = sessionRepository.findAllByAssessmentId(assessmentId)
                .stream().map(Session::getId).toList();

        List<SessionMetric> allSessionMetrics = sessionMetricRepository.findAllBySessionIdIn(allSessionIds);

        Map<Integer, List<SessionMetric>> byMetric = allSessionMetrics.stream()
                .collect(Collectors.groupingBy(SessionMetric::getConditionalMetricId));

        byMetric.forEach((conditionalMetricId, sessionMetrics) -> {
            double min = sessionMetrics.stream().mapToDouble(SessionMetric::getMinValue).min().orElse(0);
            double max = sessionMetrics.stream().mapToDouble(SessionMetric::getMaxValue).max().orElse(0);
            double avg = sessionMetrics.stream().mapToDouble(SessionMetric::getAvgValue).average().orElse(0);

            assessmentMetricRepository
                    .findByAssessmentIdAndConditionalMetricId(assessmentId, conditionalMetricId)
                    .ifPresent(am -> {
                        am.setMinValue(min);
                        am.setMaxValue(max);
                        am.setAvgValue(avg);
                        assessmentMetricRepository.save(am);
                    });
        });
    }
}
