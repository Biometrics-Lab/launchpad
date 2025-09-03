package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
public abstract class IDName {
    @Id
    protected Integer id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
}
