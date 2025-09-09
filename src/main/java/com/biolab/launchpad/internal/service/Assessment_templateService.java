package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Assessment_template;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class Assessment_templateService extends EntityService<Assessment_template> {
    @Autowired
    public Assessment_templateService(CrudRepository<Assessment_template, Integer> repository) {
        super(repository);
    }
}

