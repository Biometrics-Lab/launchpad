package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("data_source_type_dictionary")
public class Data_source_type_dictionary {
    @Id
    private String name;
    private String description;
}
