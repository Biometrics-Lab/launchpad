package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Age_group_dictionary;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class Age_group_dictionaryService extends EntityServiceName<Age_group_dictionary> {
    @Autowired
    public Age_group_dictionaryService(CrudRepository<Age_group_dictionary, String> repository) {
        super(repository);
    }
}
