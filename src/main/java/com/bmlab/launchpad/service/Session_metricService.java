package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Session_metric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Session_metricService extends EntityServiceID<Session_metric> {
    @Autowired
    public Session_metricService(CrudRepository<Session_metric, Integer> repository) {
        super(repository);
    }
}
