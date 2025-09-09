package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Report;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;


@Service
@Log4j2
public class ReportService extends EntityService<Report> {
    @Autowired
    public ReportService(CrudRepository<Report, Integer> repository) {
        super(repository);
    }
}
