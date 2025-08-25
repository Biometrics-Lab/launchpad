package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Builder
@Table("metric")
public class Metric {

    @Id
    private Integer id;// NULL → insert, NOT NULL → update
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull(message = "Measurement ID cannot be null")
    private Integer measurementId;
    private boolean negate;

}
