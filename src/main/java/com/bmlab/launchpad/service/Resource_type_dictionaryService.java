package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Resource_type_dictionary;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Resource_type_dictionaryService extends EntityServiceName<Resource_type_dictionary> {
    @Autowired
    public Resource_type_dictionaryService(CrudRepository<Resource_type_dictionary, String> repository) {
        super(repository);
    }
}
