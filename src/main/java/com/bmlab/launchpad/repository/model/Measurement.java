package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@Table("measurement")
public class Measurement extends IDName{}
