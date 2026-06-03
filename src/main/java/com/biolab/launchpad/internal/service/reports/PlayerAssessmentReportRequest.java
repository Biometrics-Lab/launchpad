package com.biolab.launchpad.internal.service.reports;

import java.util.List;

public record PlayerAssessmentReportRequest(
        Integer assessmentId,
        String granularity,
        List<Integer> conditionalMetricIds,
        Integer sessionId
) implements ReportRequestDto {}
