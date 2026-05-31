package com.biolab.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgeGroup {
    U8("8U"),
    U9("9U"),
    U10("10U"),
    U11("11U"),
    U12("12U"),
    U13("13U"),
    U14("14U"),
    HS_FRESHMAN("HSFr"),
    HS_SENIOR("HSSn");

    private final String value;
}
