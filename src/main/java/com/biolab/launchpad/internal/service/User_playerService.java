package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.User_player;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class User_playerService extends EntityServiceID<User_player> {
    @Autowired
    public User_playerService(CrudRepository<User_player, Integer> repository) {
        super(repository);
    }
}
