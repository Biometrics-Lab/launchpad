package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Rep_metric_source;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Rep_metric_sourceService extends EntityServiceID<Rep_metric_source> {
    @Autowired
    public Rep_metric_sourceService(CrudRepository<Rep_metric_source, Integer> repository) {
        super(repository);
    }
}
