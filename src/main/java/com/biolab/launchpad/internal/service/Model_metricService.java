package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Model_metric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Model_metricService extends EntityServiceID<Model_metric> {
    @Autowired
    public Model_metricService(CrudRepository<Model_metric, Integer> repository) {
        super(repository);
    }
}

