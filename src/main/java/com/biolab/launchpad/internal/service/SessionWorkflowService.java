package com.biolab.launchpad.internal.service;

import com.biolab.common.DataSourceConfig;
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

import java.util.*;
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
    private final ConditionalMetricRepository conditionalMetricRepository;
    private final MetricRepository metricRepository;
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
                            new DataSourceConfig(ds.getId(), ds.getName(), ds.getType(), ds.getContent()));
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

        Map<Integer, Boolean> negateByConditionalMetricId = buildNegateMap(session.getAssessmentId());
        computeAggregates(id, session.getAssessmentId(), negateByConditionalMetricId);

        eventPublisher.publishEvent(new SessionStoppedEvent(id));
        log.info("Session {} stopped", id);

        return sessionMapper.toDto(saved);
    }

    private Map<Integer, Boolean> buildNegateMap(Integer assessmentId) {
        List<AssessmentMetric> assessmentMetrics = assessmentMetricRepository.findAllByAssessmentId(assessmentId);

        List<ConditionalMetric> conditionalMetrics = new ArrayList<>();
        conditionalMetricRepository.findAllById(
                assessmentMetrics.stream().map(AssessmentMetric::getConditionalMetricId).toList()
        ).forEach(conditionalMetrics::add);

        Map<Integer, Integer> metricIdByCmId = conditionalMetrics.stream()
                .collect(Collectors.toMap(ConditionalMetric::getId, ConditionalMetric::getMetricId));

        Map<Integer, Boolean> negateByMetricId = new HashMap<>();
        metricRepository.findAllById(metricIdByCmId.values())
                .forEach(m -> negateByMetricId.put(m.getId(), m.isNegate()));

        return metricIdByCmId.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> negateByMetricId.getOrDefault(e.getValue(), false)));
    }

    private void computeAggregates(Integer sessionId, Integer assessmentId,
                                   Map<Integer, Boolean> negateByConditionalMetricId) {
        List<Rep> reps = repRepository.findAllBySessionId(sessionId);
        if (reps.isEmpty()) return;

        List<Integer> repIds = reps.stream().map(Rep::getId).toList();
        List<RepMetric> repMetrics = repMetricRepository.findAllByRepIdIn(repIds);

        Map<Integer, List<Number>> byMetric = repMetrics.stream()
                .collect(Collectors.groupingBy(
                        RepMetric::getConditionalMetricId,
                        Collectors.mapping(RepMetric::getValue, Collectors.toList())));

        byMetric.forEach((conditionalMetricId, values) -> {
            boolean negated = negateByConditionalMetricId.getOrDefault(conditionalMetricId, false);
            double rawMin = values.stream().mapToDouble(Number::doubleValue).min().orElse(0);
            double rawMax = values.stream().mapToDouble(Number::doubleValue).max().orElse(0);
            double avg    = values.stream().mapToDouble(Number::doubleValue).average().orElse(0);
            double min = negated ? rawMax : rawMin;
            double max = negated ? rawMin : rawMax;

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

        updateAssessmentMetrics(assessmentId, negateByConditionalMetricId);
    }

    private void updateAssessmentMetrics(Integer assessmentId,
                                         Map<Integer, Boolean> negateByConditionalMetricId) {
        List<Integer> allSessionIds = sessionRepository.findAllByAssessmentId(assessmentId)
                .stream().map(Session::getId).toList();

        List<SessionMetric> allSessionMetrics = sessionMetricRepository.findAllBySessionIdIn(allSessionIds);

        Map<Integer, List<SessionMetric>> byMetric = allSessionMetrics.stream()
                .collect(Collectors.groupingBy(SessionMetric::getConditionalMetricId));

        byMetric.forEach((conditionalMetricId, sessionMetrics) -> {
            boolean negated = negateByConditionalMetricId.getOrDefault(conditionalMetricId, false);
            double min = negated
                    ? sessionMetrics.stream().mapToDouble(SessionMetric::getMinValue).max().orElse(0)
                    : sessionMetrics.stream().mapToDouble(SessionMetric::getMinValue).min().orElse(0);
            double max = negated
                    ? sessionMetrics.stream().mapToDouble(SessionMetric::getMaxValue).min().orElse(0)
                    : sessionMetrics.stream().mapToDouble(SessionMetric::getMaxValue).max().orElse(0);
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
