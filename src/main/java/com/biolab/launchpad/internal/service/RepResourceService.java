package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.RepResourceRepository;
import com.biolab.launchpad.internal.repository.model.RepResource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class RepResourceService extends EntityServiceID<RepResource> {

    private final RepResourceRepository repResourceRepository;

    @Autowired
    public RepResourceService(RepResourceRepository repository) {
        super(repository);
        this.repResourceRepository = repository;
    }

    public List<RepResource> findAllByRepId(Integer repId) {
        return repResourceRepository.findAllByRepId(repId);
    }
}
