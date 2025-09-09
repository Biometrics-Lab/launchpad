package com.bmlab.launchpad.repository;

import com.bmlab.launchpad.repository.model.Rep;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepRepository extends CrudRepository<Rep, Integer> {
}
