package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Template_metric;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Template_metricService extends EntityServiceID<Template_metric> {
    @Autowired
    public Template_metricService(CrudRepository<Template_metric, Integer> repository) {
        super(repository);
    }
}
