package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Assessment_resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Assessment_resourceService extends EntityServiceID<Assessment_resource> {
    @Autowired
    public Assessment_resourceService(CrudRepository<Assessment_resource, Integer> repository) {
        super(repository);
    }
}

