package com.biolab.launchpad.internal.web.dto;

import com.biolab.common.UrlStatus;

public record RepResourceBroadcastData(
        Integer id,
        String type,
        String url,
        UrlStatus status
) {}
