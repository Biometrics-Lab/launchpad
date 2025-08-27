package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.MeasurementRepository;
import com.bmlab.launchpad.repository.model.Measurement;
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
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    public Measurement create(Measurement measurement) {
        try {
            return measurementRepository.save(measurement);
        } catch (Exception ex) {
            log.warn("Could not create measurement: {}", measurement.toString());
            throw new PersistException(ex.getMessage());
        }
    }

    public List<Measurement> findAll() {
        return (List<Measurement>) measurementRepository.findAll();
    }

    public Optional<Measurement> findById(Integer id) {
        try {
            return measurementRepository.findById(id);
        } catch (Exception ex) {
            log.warn("Could not find measurement by id: {}", id);
            throw new PersistException(ex.getMessage());
        }
    }

    public void deleteById(Integer id) {
        try {
            if (measurementRepository.existsById(id)) {
                measurementRepository.deleteById(id);
            } else {
                throw new NotFoundByException("Measurement not deleted with id: %d", id);
            }
        } catch (NotFoundByException ex) {
            log.warn("Could not find measurement by id: {}", id);
            throw ex;
        } catch (Exception ex) {
            log.warn("Could not delete measurement by id: {}", id);
            throw new PersistException(ex.getMessage());
        }
    }

    public Measurement update(Measurement measurement) {
        try {
            if (measurementRepository.existsById(measurement.getId())) {
                return measurementRepository.save(measurement);
            } else {
                throw new NotFoundByException("Measurement not updated with id: %d", measurement.getId());
            }
        } catch (NotFoundByException ex) {
            log.warn("Could not update measurement by id: {}", measurement.getId());
            throw ex;
        } catch (Exception ex) {
            log.warn("Could not update measurement: {}", measurement.toString());
            throw new PersistException(ex.getMessage());
        }
    }
}
