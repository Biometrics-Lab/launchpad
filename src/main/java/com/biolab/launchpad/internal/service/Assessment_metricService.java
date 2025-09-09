package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Assessment_metric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Assessment_metricService extends EntityServiceID<Assessment_metric> {
    @Autowired
    public Assessment_metricService(CrudRepository<Assessment_metric, Integer> repository) {
        super(repository);
    }
}

