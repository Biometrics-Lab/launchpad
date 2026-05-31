package com.biolab.launchpad.internal.repository.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("report")
public class Report extends IDName {
    @Column("report_type")
    private String reportType;

    @Column("config")
    private String config;
}
