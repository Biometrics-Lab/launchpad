package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.ReportRepository;
import com.biolab.launchpad.internal.repository.model.Report;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class ReportService extends EntityService<Report> {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository repository) {
        super(repository);
        this.reportRepository = repository;
    }

    public Optional<Report> findByName(String name) {
        return reportRepository.findByName(name);
    }

    public List<Report> findAllByReportType(String reportType) {
        return reportRepository.findAllByReportType(reportType);
    }

    public boolean existsByName(String name) {
        return reportRepository.findByName(name).isPresent();
    }
}
