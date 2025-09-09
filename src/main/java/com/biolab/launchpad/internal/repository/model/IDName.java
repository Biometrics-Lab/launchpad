package com.biolab.launchpad.internal.repository.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

@Data
@SuperBuilder
public abstract class IDName {
    @Id
    protected Integer id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
}
