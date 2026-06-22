package com.biolab.launchpad.internal.service;

import com.biolab.common.RepDataReceivedEvent;
import com.biolab.common.ResourceContentType;
import com.biolab.launchpad.internal.repository.AssessmentMetricRepository;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.SessionRepository;
import com.biolab.launchpad.internal.repository.model.AssessmentMetric;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.repository.model.RepMetric;
import com.biolab.launchpad.internal.repository.model.RepResource;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class RepEventService {

    private final SessionRepository sessionRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;
    private final RepRepository repRepository;
    private final RepMetricRepository repMetricRepository;
    private final RepResourceRepository repResourceRepository;
    private final RepService repService;
    private final SessionNotificationService sessionNotificationService;

    @ApplicationModuleListener
    public void onRepDataReceived(RepDataReceivedEvent event) {
        log.info("RepDataReceivedEvent received for session={} rep={}", event.sessionId(), event.repNumber());

        var session = sessionRepository.findById(event.sessionId())
                .orElseThrow(() -> new NotFoundByException("Session not found by id: %d", event.sessionId()));

        Set<Integer> validMetricIds = assessmentMetricRepository
                .findAllByAssessmentId(session.getAssessmentId())
                .stream()
                .map(AssessmentMetric::getConditionalMetricId)
                .collect(Collectors.toSet());

        Rep rep = repRepository.findBySessionIdAndRepNumber(event.sessionId(), event.repNumber())
                .orElseGet(() -> repRepository.save(Rep.builder()
                        .sessionId(event.sessionId())
                        .startTime(event.startTime())
                        .repNumber(event.repNumber())
                        .build()));

        List<RepMetric> savedMetrics = event.metrics().stream()
                .filter(m -> validMetricIds.contains(m.conditionalMetricId()))
                .map(m -> repMetricRepository.save(RepMetric.builder()
                        .repId(rep.getId())
                        .conditionalMetricId(m.conditionalMetricId())
                        .dataSourceId(m.dataSourceId())
                        .value(m.value())
                        .build()))
                .collect(Collectors.toList());

        List<RepResource> savedResources = event.resources().stream()
                .map(r -> repResourceRepository.save(RepResource.builder()
                        .repId(rep.getId())
                        .url(r.url())
                        .type(ResourceContentType.VIDEO.getValue())
                        .urlStatus(r.urlStatus())
                        .uuid(r.uuid())
                        .build()))
                .collect(Collectors.toList());

        sessionNotificationService.broadcastRep(event.sessionId(), repService.buildDto(rep, savedMetrics, savedResources));
    }
}
