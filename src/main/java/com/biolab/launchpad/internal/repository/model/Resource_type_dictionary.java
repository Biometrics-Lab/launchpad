package com.biolab.launchpad.internal.repository.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("resource_type_dictionary")
public class Resource_type_dictionary extends Name{
    private String description;
}
