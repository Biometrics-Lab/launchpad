package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerMetricsService {

    private final PlayerRepository playerRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;
    private final ConditionalMetricRepository conditionalMetricRepository;
    private final MetricRepository metricRepository;

    public PlayerMetricsDto getMetrics(Integer playerId) {
        playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundByException("Player not found by id: %d", playerId));

        List<Assessment> assessments = assessmentRepository.findAllByPlayerId(playerId);
        if (assessments.isEmpty()) {
            return PlayerMetricsDto.builder().overall(List.of()).assessments(List.of()).build();
        }

        List<Assessment> sorted = assessments.stream()
                .sorted(Comparator.comparingInt(Assessment::getId))
                .toList();

        Map<Integer, List<AssessmentMetric>> amsByAssessmentId = new LinkedHashMap<>();
        for (Assessment a : sorted) {
            amsByAssessmentId.put(a.getId(), assessmentMetricRepository.findAllByAssessmentId(a.getId()));
        }

        List<AssessmentMetric> allAms = amsByAssessmentId.values().stream()
                .flatMap(Collection::stream)
                .toList();

        Set<Integer> cmIds = allAms.stream()
                .map(AssessmentMetric::getConditionalMetricId)
                .collect(Collectors.toSet());
        Map<Integer, ConditionalMetric> cmById = new HashMap<>();
        conditionalMetricRepository.findAllById(cmIds).forEach(cm -> cmById.put(cm.getId(), cm));

        Set<Integer> metricIds = cmById.values().stream()
                .map(ConditionalMetric::getMetricId)
                .collect(Collectors.toSet());
        Map<Integer, Metric> metricById = new HashMap<>();
        metricRepository.findAllById(metricIds).forEach(m -> metricById.put(m.getId(), m));

        List<AssessmentMetricsDto> assessmentDtos = sorted.stream()
                .map(assessment -> {
                    List<MetricAggregateDto> metrics = amsByAssessmentId.get(assessment.getId()).stream()
                            .sorted(Comparator.comparingInt(AssessmentMetric::getConditionalMetricId))
                            .map(am -> toMetricAggregateDto(am, cmById, metricById))
                            .toList();
                    return AssessmentMetricsDto.builder()
                            .assessmentId(assessment.getId())
                            .sport(assessment.getSport())
                            .metrics(metrics)
                            .build();
                })
                .toList();

        List<MetricAggregateDto> overall = computeOverall(allAms, cmById, metricById);

        return PlayerMetricsDto.builder()
                .overall(overall)
                .assessments(assessmentDtos)
                .build();
    }

    private MetricAggregateDto toMetricAggregateDto(AssessmentMetric am,
                                                     Map<Integer, ConditionalMetric> cmById,
                                                     Map<Integer, Metric> metricById) {
        ConditionalMetric cm = cmById.get(am.getConditionalMetricId());
        Metric metric = cm != null ? metricById.get(cm.getMetricId()) : null;
        boolean negated = metric != null && metric.isNegate();
        return MetricAggregateDto.builder()
                .conditionalMetricId(am.getConditionalMetricId())
                .name(cm != null ? cm.getName() : null)
                .negated(negated)
                .minValue(am.getMinValue() != null ? am.getMinValue().doubleValue() : null)
                .maxValue(am.getMaxValue() != null ? am.getMaxValue().doubleValue() : null)
                .avgValue(am.getAvgValue() != null ? am.getAvgValue().doubleValue() : null)
                .build();
    }

    private List<MetricAggregateDto> computeOverall(List<AssessmentMetric> allAms,
                                                      Map<Integer, ConditionalMetric> cmById,
                                                      Map<Integer, Metric> metricById) {
        Map<Integer, List<AssessmentMetric>> byCmId = allAms.stream()
                .collect(Collectors.groupingBy(AssessmentMetric::getConditionalMetricId));

        return byCmId.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    int cmId = entry.getKey();
                    List<AssessmentMetric> ams = entry.getValue();
                    ConditionalMetric cm = cmById.get(cmId);
                    Metric metric = cm != null ? metricById.get(cm.getMetricId()) : null;
                    boolean negated = metric != null && metric.isNegate();

                    List<Double> minValues = ams.stream()
                            .filter(am -> am.getMinValue() != null)
                            .map(am -> am.getMinValue().doubleValue())
                            .toList();
                    List<Double> maxValues = ams.stream()
                            .filter(am -> am.getMaxValue() != null)
                            .map(am -> am.getMaxValue().doubleValue())
                            .toList();
                    List<Double> avgValues = ams.stream()
                            .filter(am -> am.getAvgValue() != null)
                            .map(am -> am.getAvgValue().doubleValue())
                            .toList();

                    OptionalDouble minOpt = negated
                            ? minValues.stream().mapToDouble(Double::doubleValue).max()
                            : minValues.stream().mapToDouble(Double::doubleValue).min();
                    OptionalDouble maxOpt = negated
                            ? maxValues.stream().mapToDouble(Double::doubleValue).min()
                            : maxValues.stream().mapToDouble(Double::doubleValue).max();
                    OptionalDouble avgOpt = avgValues.stream().mapToDouble(Double::doubleValue).average();

                    return MetricAggregateDto.builder()
                            .conditionalMetricId(cmId)
                            .name(cm != null ? cm.getName() : null)
                            .negated(negated)
                            .minValue(minOpt.isPresent() ? minOpt.getAsDouble() : null)
                            .maxValue(maxOpt.isPresent() ? maxOpt.getAsDouble() : null)
                            .avgValue(avgOpt.isPresent() ? avgOpt.getAsDouble() : null)
                            .build();
                })
                .toList();
    }
}
