package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.User_role_dictionary;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class User_roles_dictionaryService extends EntityServiceName<User_role_dictionary> {
    @Autowired
    public User_roles_dictionaryService(CrudRepository<User_role_dictionary, String> repository) {
        super(repository);
    }
}
