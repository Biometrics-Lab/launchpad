package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.Metric;
import com.bmlab.launchpad.repository.MetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;

    public Metric create(Metric metric) {
        return metricRepository.save(metric);
    }

    public Iterable<Metric> findAll() {
        return metricRepository.findAll();
    }
    public Optional<Metric> findById(Integer id) {
        return metricRepository.findById(id);
    }

    public void deleteById(Integer id) {
        metricRepository.deleteById(id);
    }

    public Metric update(Metric metric) {
        return metricRepository.save(metric);
    }
}
