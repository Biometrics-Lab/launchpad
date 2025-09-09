package com.biolab.launchpad.internal.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.w3c.dom.Text;

@Builder
public record Assessment_templateDto (
        Integer id,
        @NotBlank(message = "Name cannot be blank")
        String name,
        @NotNull(message = "Assessment_template sport cannot be null")
        String sport,
        Text description
){}
