package com.bmlab.launchpad.repository.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

@Data
@SuperBuilder
@NoArgsConstructor
@Table("sport_dictionary")
public class Sport_dictionary extends Name{
    private String description;
}
