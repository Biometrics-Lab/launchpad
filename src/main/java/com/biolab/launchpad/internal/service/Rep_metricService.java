package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Rep_metric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Rep_metricService extends EntityServiceID<Rep_metric> {
    @Autowired
    public Rep_metricService(CrudRepository<Rep_metric, Integer> repository) {
        super(repository);
    }
}
