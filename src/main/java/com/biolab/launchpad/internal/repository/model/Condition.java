package com.biolab.launchpad.internal.repository.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("condition")
public class Condition extends IDName {
    private String sport;
    @Column("template_id")
    private Integer templateId;
}
