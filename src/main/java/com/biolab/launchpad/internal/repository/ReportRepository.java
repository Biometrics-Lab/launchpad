package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Report;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends CrudRepository<Report, Integer> {
    Optional<Report> findByName(String name);
    List<Report> findAllByReportType(String reportType);
}
