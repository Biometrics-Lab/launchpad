package com.biolab.launchpad.internal.repository.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class Name implements Persistable<String> {
    @Id
    protected String name;

    @Transient
    private boolean newObject;

    @Override
    public String getId() {
        return name;
    }

    @Override
    public boolean isNew() {
        return newObject;
    }

    public void markAsNew(boolean value) {
        this.newObject = value;
    }


}
