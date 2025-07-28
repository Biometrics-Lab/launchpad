package com.bmlab.launchpad.repository;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "id")
@Table("metric")
public class Metric {

    @Id
    private Integer id;// NULL → insert, NOT NULL → update
    @NonNull
    private String name;
    private Integer measurementId;
    private Boolean negate;

    public Metric() {}

}

