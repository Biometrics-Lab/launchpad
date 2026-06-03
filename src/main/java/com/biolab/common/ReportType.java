package com.biolab.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReportType {
    PLAYER_ASSESSMENT("player-assessment");

    private final String value;
}
