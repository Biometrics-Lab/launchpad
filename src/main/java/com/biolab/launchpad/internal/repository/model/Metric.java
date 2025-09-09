package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;


@Data
@SuperBuilder
@Table("metric")
public class Metric extends IDName {
    @NotNull(message = "Measurement ID cannot be null")
    private Integer measurementId;
    private boolean negate;
}
