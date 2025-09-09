package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Session_resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Session_resourceService extends EntityServiceID<Session_resource> {
    @Autowired
    public Session_resourceService(CrudRepository<Session_resource, Integer> repository) {
        super(repository);
    }
}
