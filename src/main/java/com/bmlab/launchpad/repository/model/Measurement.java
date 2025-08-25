package com.bmlab.launchpad.repository.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@Table("measurement")
public class Measurement {
    @Id
    private Integer id;
    @NotBlank(message = "Name cannot be blank")
    private String name;
}
