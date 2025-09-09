package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.User_role_dictionary;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class User_role_dictionaryService extends EntityServiceName<User_role_dictionary> {
    @Autowired
    public User_role_dictionaryService(CrudRepository<User_role_dictionary, String> repository) {
        super(repository);
    }
}
