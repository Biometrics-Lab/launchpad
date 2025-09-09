package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Data_source;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Data_sourceService extends EntityService<Data_source> {
    @Autowired
    public Data_sourceService(CrudRepository<Data_source, Integer> repository) {
        super(repository);
    }
}

