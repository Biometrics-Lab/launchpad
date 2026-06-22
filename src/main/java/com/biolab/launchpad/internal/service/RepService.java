package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ConditionalMetricRepository;
import com.biolab.launchpad.internal.repository.RepMetricRepository;
import com.biolab.launchpad.internal.repository.RepRepository;
import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.ConditionalMetric;
import com.biolab.launchpad.internal.repository.model.Rep;
import com.biolab.launchpad.internal.repository.model.RepMetric;
import com.biolab.launchpad.internal.repository.model.RepResource;
import com.biolab.launchpad.internal.web.dto.RepBroadcastDto;
import com.biolab.launchpad.internal.web.dto.RepMetricBroadcastData;
import com.biolab.launchpad.internal.web.dto.RepResourceBroadcastData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RepService extends EntityServiceID<Rep> {

    private final RepRepository repRepository;
    private final RepMetricRepository repMetricRepository;
    private final RepResourceRepository repResourceRepository;
    private final ConditionalMetricRepository conditionalMetricRepository;

    @Autowired
    public RepService(RepRepository repository,
                      RepMetricRepository repMetricRepository,
                      RepResourceRepository repResourceRepository,
                      ConditionalMetricRepository conditionalMetricRepository) {
        super(repository);
        this.repRepository = repository;
        this.repMetricRepository = repMetricRepository;
        this.repResourceRepository = repResourceRepository;
        this.conditionalMetricRepository = conditionalMetricRepository;
    }

    public RepBroadcastDto buildDto(Rep rep, List<RepMetric> metrics, List<RepResource> resources) {
        Set<Integer> cmIds = metrics.stream()
                .map(RepMetric::getConditionalMetricId)
                .collect(Collectors.toSet());
        return assembleDto(rep, metrics, fetchCmNames(cmIds), resources);
    }

    public List<RepBroadcastDto> getBroadcastDtosForSession(Integer sessionId) {
        List<Rep> reps = repRepository.findAllBySessionId(sessionId);
        if (reps.isEmpty()) return List.of();

        List<Integer> repIds = reps.stream().map(Rep::getId).collect(Collectors.toList());
        List<RepMetric> allMetrics = repMetricRepository.findAllByRepIdIn(repIds);

        Map<Integer, String> cmNames = fetchCmNames(allMetrics.stream()
                .map(RepMetric::getConditionalMetricId)
                .collect(Collectors.toSet()));

        Map<Integer, List<RepMetric>> metricsByRepId = allMetrics.stream()
                .collect(Collectors.groupingBy(RepMetric::getRepId));

        return reps.stream()
                .map(rep -> assembleDto(
                        rep,
                        metricsByRepId.getOrDefault(rep.getId(), List.of()),
                        cmNames,
                        repResourceRepository.findAllByRepId(rep.getId())))
                .collect(Collectors.toList());
    }

    private Map<Integer, String> fetchCmNames(Collection<Integer> cmIds) {
        if (cmIds.isEmpty()) return Map.of();
        return conditionalMetricRepository.findAllByIdIn(cmIds).stream()
                .collect(Collectors.toMap(ConditionalMetric::getId, ConditionalMetric::getName));
    }

    private RepBroadcastDto assembleDto(Rep rep, List<RepMetric> metrics,
                                        Map<Integer, String> cmNames, List<RepResource> resources) {
        List<RepMetricBroadcastData> metricData = metrics.stream()
                .map(m -> new RepMetricBroadcastData(
                        m.getConditionalMetricId(),
                        cmNames.get(m.getConditionalMetricId()),
                        m.getValue()))
                .collect(Collectors.toList());

        List<RepResourceBroadcastData> resourceData = resources.stream()
                .map(r -> new RepResourceBroadcastData(r.getId(), r.getType(), r.getUrl(), r.getUrlStatus()))
                .collect(Collectors.toList());

        return new RepBroadcastDto(rep.getId(), rep.getSessionId(), rep.getRepNumber(),
                rep.getStartTime(), metricData, resourceData);
    }
}
