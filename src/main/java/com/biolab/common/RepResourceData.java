package com.biolab.common;

import java.util.UUID;

public record RepResourceData(
        String url,
        UrlStatus urlStatus,
        UUID uuid
) {}
