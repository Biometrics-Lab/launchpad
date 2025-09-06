package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Rep_resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Rep_resourceService extends EntityServiceID<Rep_resource> {
    @Autowired
    public Rep_resourceService(CrudRepository<Rep_resource, Integer> repository) {
        super(repository);
    }
}
