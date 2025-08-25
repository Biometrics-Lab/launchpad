package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.Metric;
import com.bmlab.launchpad.repository.MetricRepository;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.security.exceptions.PersistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Log4j2
public class MetricService {

    private final MetricRepository metricRepository;

    public Metric create(Metric metric) {
        try {
            return metricRepository.save(metric);
        } catch (Exception ex) {
            log.warn("Could not create metric: {}", metric.toString());
            throw new PersistException(ex.getMessage());
        }
    }

    public List<Metric> findAll() {
        return (List<Metric>) metricRepository.findAll();
    }

    public Optional<Metric> findById(Integer id) {
        try {
            return metricRepository.findById(id);
        } catch (Exception ex) {
            log.warn("Could not find metric by id: {}", id);
            throw new PersistException(ex.getMessage());
        }
    }

    public void deleteById(Integer id) {
        try {
            if (metricRepository.existsById(id)) {
                metricRepository.deleteById(id);
            } else {
                throw new NotFoundByException("Metric not deleted with id: %d", id);
            }
        } catch (NotFoundByException ex) {
            log.warn("Could not find metric by id: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.warn("Could not delete metric by id: {}", id);
            throw new PersistException(ex.getMessage());
        }
    }

    public Metric update(Metric metric) {
        try {
            if (metricRepository.existsById(metric.getId())) {
                return metricRepository.save(metric);
            } else {
                throw new NotFoundByException("Metric not updated with id: %d", metric.getId());
            }
        } catch (NotFoundByException ex) {
            log.warn("Could not update metric by id: {}", metric.getId());
            throw ex;
        } catch (Exception ex) {
            log.warn("Could not update metric: {}", metric.toString());
            throw new PersistException(ex.getMessage());
        }
    }
}
