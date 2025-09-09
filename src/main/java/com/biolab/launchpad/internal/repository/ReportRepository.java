package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends CrudRepository<Report, Integer> {
}
