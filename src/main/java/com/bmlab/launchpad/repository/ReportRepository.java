package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends CrudRepository<Report, Integer> {
}
