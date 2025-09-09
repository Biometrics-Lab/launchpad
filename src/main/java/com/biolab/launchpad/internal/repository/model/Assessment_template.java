package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;
import org.w3c.dom.Text;


@Data
@SuperBuilder
@NoArgsConstructor
@Table("assessment_template")
public class Assessment_template extends IDName {
    @NotNull(message = "Assessment_template sport cannot be null")
    private String sport;
    private Text description;
}
