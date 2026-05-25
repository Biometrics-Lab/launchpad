package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Assessment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends CrudRepository<Assessment, Integer> {
    List<Assessment> findAllByPlayerId(Integer playerId);
}
