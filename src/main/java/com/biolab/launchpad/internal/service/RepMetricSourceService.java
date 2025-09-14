package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.RepMetricSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class RepMetricSourceService extends EntityServiceID<RepMetricSource> {
    @Autowired
    public RepMetricSourceService(CrudRepository<RepMetricSource, Integer> repository) {
        super(repository);
    }
}
