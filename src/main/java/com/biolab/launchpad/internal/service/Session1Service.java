package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Session;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Session1Service extends EntityServiceID<Session> {
    @Autowired
    public Session1Service(CrudRepository<Session, Integer> repository) {
        super(repository);
    }
}
