package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Team;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class TeamService extends EntityService<Team> {
    @Autowired
    public TeamService(CrudRepository<Team, Integer> repository) {
        super(repository);
    }
}
