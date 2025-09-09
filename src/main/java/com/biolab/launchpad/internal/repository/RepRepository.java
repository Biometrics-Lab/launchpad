package com.biolab.launchpad.internal.repository;

import com.biolab.launchpad.internal.repository.model.Rep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepRepository extends CrudRepository<Rep, Integer> {
}
