package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Sport_dictionary;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Sport_dictionaryService extends EntityServiceName<Sport_dictionary> {
    @Autowired
    public Sport_dictionaryService(CrudRepository<Sport_dictionary, String> repository) {
        super(repository);
    }
}
