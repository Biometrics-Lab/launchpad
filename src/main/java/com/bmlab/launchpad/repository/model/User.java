package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;


@Data
@SuperBuilder
@NoArgsConstructor
@Table("user")
public class User extends IDName {
    @NotNull(message = "User role cannot be null")
    private Integer role;
}
