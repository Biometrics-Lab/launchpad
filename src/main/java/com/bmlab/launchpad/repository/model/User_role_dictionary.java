package com.bmlab.launchpad.repository.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("user_role_dictionary")
public class User_role_dictionary {
    @Id
    private String name;
    private String description;
}
