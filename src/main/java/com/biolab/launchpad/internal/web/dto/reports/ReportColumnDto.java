package com.biolab.launchpad.internal.web.dto.reports;

import lombok.Builder;

@Builder
public record ReportColumnDto(Integer id, String label, String type) {}
