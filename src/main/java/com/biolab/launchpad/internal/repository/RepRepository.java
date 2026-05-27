package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Rep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepRepository extends CrudRepository<Rep, Integer> {
    List<Rep> findAllBySessionId(Integer sessionId);
    Optional<Rep> findBySessionIdAndRepNumber(Integer sessionId, Integer repNumber);
}
