package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class IDName {
    @Id
    protected Integer id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
}
