package com.biolab.launchpad.internal.repository.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("user_roles_dictionary")
public class User_role_dictionary extends Name {
    private String description;
}
