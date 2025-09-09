package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Data_source_type_dictionary;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Data_source_type_dictionaryService extends EntityServiceName<Data_source_type_dictionary> {
    @Autowired
    public Data_source_type_dictionaryService(CrudRepository<Data_source_type_dictionary, String> repository) {
        super(repository);
    }
}

