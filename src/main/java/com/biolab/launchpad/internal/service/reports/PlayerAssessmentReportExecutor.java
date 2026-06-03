package com.biolab.launchpad.internal.service.reports;

import com.biolab.common.ReportGranularity;
import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.web.dto.reports.*;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PlayerAssessmentReportExecutor implements ReportExecutor {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentMetricRepository assessmentMetricRepository;
    private final SessionRepository sessionRepository;
    private final SessionMetricRepository sessionMetricRepository;
    private final RepRepository repRepository;
    private final RepMetricRepository repMetricRepository;
    private final ConditionalMetricRepository conditionalMetricRepository;

    @Override
    public String reportType() {
        return "player-assessment";
    }

    @Override
    public ReportDataDto execute(Integer assessmentId, ReportConfigService.ResolvedConfig config) {
        assessmentRepository.findById(assessmentId)
            .orElseThrow(() -> new NotFoundByException("Assessment not found by id: %d", assessmentId));

        ReportGranularity granularity = extractGranularity(config.config());
        List<Integer> cmFilter = extractCmFilter(config.config());

        return switch (granularity) {
            case OVERALL -> buildOverall(assessmentId, cmFilter);
            case PER_SESSION -> buildPerSession(assessmentId, cmFilter);
            case PER_REP -> buildPerRep(assessmentId, cmFilter);
        };
    }

    private ReportGranularity extractGranularity(JsonNode config) {
        String g = config.path("granularity").asText("OVERALL");
        try {
            return ReportGranularity.valueOf(g);
        } catch (IllegalArgumentException e) {
            return ReportGranularity.OVERALL;
        }
    }

    private List<Integer> extractCmFilter(JsonNode config) {
        List<Integer> ids = new ArrayList<>();
        JsonNode cmIds = config.path("filters").path("conditionalMetricIds");
        if (cmIds.isArray()) {
            cmIds.forEach(n -> ids.add(n.asInt()));
        }
        return ids;
    }

    // ── OVERALL ──────────────────────────────────────────────────────────

    private ReportDataDto buildOverall(Integer assessmentId, List<Integer> cmFilter) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow();

        ReportColumnDto column = ReportColumnDto.builder()
            .id(assessmentId)
            .label("#" + assessmentId + " " + assessment.getSport())
            .type("assessment")
            .build();

        List<AssessmentMetric> metrics = assessmentMetricRepository.findAllByAssessmentId(assessmentId);
        if (!cmFilter.isEmpty()) {
            metrics = metrics.stream()
                .filter(m -> cmFilter.contains(m.getConditionalMetricId()))
                .toList();
        }

        Map<Integer, ConditionalMetric> cmById = loadCmById(
            metrics.stream().map(AssessmentMetric::getConditionalMetricId).collect(Collectors.toSet())
        );

        List<ReportRowDto> rows = metrics.stream()
            .sorted(Comparator.comparingInt(AssessmentMetric::getConditionalMetricId))
            .map(am -> {
                ConditionalMetric cm = cmById.get(am.getConditionalMetricId());
                ReportCellDto cell = ReportCellDto.builder()
                    .columnId(assessmentId)
                    .min(am.getMinValue() != null ? am.getMinValue().doubleValue() : null)
                    .max(am.getMaxValue() != null ? am.getMaxValue().doubleValue() : null)
                    .avg(am.getAvgValue() != null ? am.getAvgValue().doubleValue() : null)
                    .count(am.getSessionCount())
                    .build();
                return ReportRowDto.builder()
                    .conditionalMetricId(am.getConditionalMetricId())
                    .name(cm != null ? cm.getName() : null)
                    .negated(false)
                    .cells(List.of(cell))
                    .build();
            })
            .toList();

        return ReportDataDto.builder().columns(List.of(column)).rows(rows).build();
    }

    // ── PER_SESSION ───────────────────────────────────────────────────────

    private ReportDataDto buildPerSession(Integer assessmentId, List<Integer> cmFilter) {
        List<Session> sessions = sessionRepository.findAllByAssessmentId(assessmentId);
        sessions.sort(Comparator.comparing(Session::getStartTime));

        List<ReportColumnDto> columns = new ArrayList<>();
        for (int i = 0; i < sessions.size(); i++) {
            Session s = sessions.get(i);
            String date = s.getStartTime().toLocalDateTime().toLocalDate().toString();
            columns.add(ReportColumnDto.builder()
                .id(s.getId())
                .label("Session " + (i + 1) + " — " + date)
                .type("session")
                .build());
        }

        List<Integer> sessionIds = sessions.stream().map(Session::getId).toList();
        if (sessionIds.isEmpty()) {
            return ReportDataDto.builder().columns(columns).rows(List.of()).build();
        }

        List<SessionMetric> allMetrics = sessionMetricRepository.findAllBySessionIdIn(sessionIds);
        if (!cmFilter.isEmpty()) {
            allMetrics = allMetrics.stream()
                .filter(m -> cmFilter.contains(m.getConditionalMetricId()))
                .toList();
        }

        Map<Integer, List<SessionMetric>> byCmId = allMetrics.stream()
            .collect(Collectors.groupingBy(SessionMetric::getConditionalMetricId));

        Map<Integer, ConditionalMetric> cmById = loadCmById(byCmId.keySet());

        List<ReportRowDto> rows = byCmId.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                int cmId = entry.getKey();
                ConditionalMetric cm = cmById.get(cmId);
                Map<Integer, SessionMetric> bySessionId = entry.getValue().stream()
                    .collect(Collectors.toMap(SessionMetric::getSessionId, sm -> sm));

                List<ReportCellDto> cells = sessions.stream()
                    .filter(s -> bySessionId.containsKey(s.getId()))
                    .map(s -> {
                        SessionMetric sm = bySessionId.get(s.getId());
                        return ReportCellDto.builder()
                            .columnId(s.getId())
                            .min(sm.getMinValue())
                            .max(sm.getMaxValue())
                            .avg(sm.getAvgValue())
                            .count(1)
                            .build();
                    })
                    .toList();

                return ReportRowDto.builder()
                    .conditionalMetricId(cmId)
                    .name(cm != null ? cm.getName() : null)
                    .negated(false)
                    .cells(cells)
                    .build();
            })
            .toList();

        return ReportDataDto.builder().columns(columns).rows(rows).build();
    }

    // ── PER_REP ───────────────────────────────────────────────────────────

    private ReportDataDto buildPerRep(Integer assessmentId, List<Integer> cmFilter) {
        List<Session> sessions = sessionRepository.findAllByAssessmentId(assessmentId);
        sessions.sort(Comparator.comparing(Session::getStartTime));

        List<Rep> allReps = new ArrayList<>();
        for (Session s : sessions) {
            List<Rep> reps = repRepository.findAllBySessionId(s.getId());
            reps.sort(Comparator.comparingInt(r -> r.getRepNumber() != null ? r.getRepNumber() : 0));
            allReps.addAll(reps);
        }

        List<ReportColumnDto> columns = new ArrayList<>();
        for (int i = 0; i < allReps.size(); i++) {
            columns.add(ReportColumnDto.builder()
                .id(allReps.get(i).getId())
                .label("Rep " + (i + 1))
                .type("rep")
                .build());
        }

        List<Integer> repIds = allReps.stream().map(Rep::getId).toList();
        if (repIds.isEmpty()) {
            return ReportDataDto.builder().columns(columns).rows(List.of()).build();
        }

        List<RepMetric> allMetrics = repMetricRepository.findAllByRepIdIn(repIds);
        if (!cmFilter.isEmpty()) {
            allMetrics = allMetrics.stream()
                .filter(m -> cmFilter.contains(m.getConditionalMetricId()))
                .toList();
        }

        Map<Integer, List<RepMetric>> byCmId = allMetrics.stream()
            .collect(Collectors.groupingBy(RepMetric::getConditionalMetricId));

        Map<Integer, ConditionalMetric> cmById = loadCmById(byCmId.keySet());

        List<ReportRowDto> rows = byCmId.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                int cmId = entry.getKey();
                ConditionalMetric cm = cmById.get(cmId);
                Map<Integer, RepMetric> byRepId = entry.getValue().stream()
                    .collect(Collectors.toMap(RepMetric::getRepId, rm -> rm));

                List<ReportCellDto> cells = allReps.stream()
                    .filter(r -> byRepId.containsKey(r.getId()))
                    .map(r -> {
                        double val = byRepId.get(r.getId()).getValue().doubleValue();
                        return ReportCellDto.builder()
                            .columnId(r.getId())
                            .min(val)
                            .max(val)
                            .avg(val)
                            .count(1)
                            .build();
                    })
                    .toList();

                return ReportRowDto.builder()
                    .conditionalMetricId(cmId)
                    .name(cm != null ? cm.getName() : null)
                    .negated(false)
                    .cells(cells)
                    .build();
            })
            .toList();

        return ReportDataDto.builder().columns(columns).rows(rows).build();
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Map<Integer, ConditionalMetric> loadCmById(Set<Integer> ids) {
        Map<Integer, ConditionalMetric> map = new HashMap<>();
        conditionalMetricRepository.findAllById(ids).forEach(cm -> map.put(cm.getId(), cm));
        return map;
    }
}
