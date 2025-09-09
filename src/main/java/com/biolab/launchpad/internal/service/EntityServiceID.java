package com.bmlab.launchpad.service;

import com.bmlab.launchpad.repository.model.ID;
import com.bmlab.launchpad.security.exceptions.NotFoundByException;
import com.bmlab.launchpad.security.exceptions.PersistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Log4j2
public class EntityServiceID<T extends ID> {

    protected final CrudRepository<T, Integer> repository;

    public T create(T entity) {
        String serviceName = getClass().getSimpleName();
        String entityName = entity.getClass().getSimpleName();
        try {
            return repository.save(entity);
        } catch (Exception ex) {
            log.warn("{}. Could not create {}: {}", serviceName, entityName, entity.toString());
            throw new PersistException(ex.getMessage());
        }
    }

    public List<T> findAll() {
        return (List<T>) repository.findAll();
    }

    public Optional<T> findById(Integer id) {
        String serviceName = getClass().getSimpleName();
        try {
            return repository.findById(id);
        } catch (Exception ex) {
            log.warn("{}. Could not find by id: {}", serviceName, id);
            throw new PersistException(ex.getMessage());
        }
    }

    public void deleteById(Integer id) {
        String serviceName = getClass().getSimpleName();
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
            } else {
                throw new NotFoundByException("%s. Could not delete id: %d", serviceName, id);
            }
        } catch (NotFoundByException ex) {
            log.warn("{}. Could not find by id: {}", serviceName, id);
            throw ex;
        } catch (Exception ex) {
            log.warn("{}. Could not delete by id: {}", serviceName, id);
            throw new PersistException(ex.getMessage());
        }
    }

    public T update(T entity) {
        String serviceName = getClass().getSimpleName();
        String entityName = entity.getClass().getSimpleName();
        try {
            if (repository.existsById(entity.getId())) {
                return repository.save(entity);
            } else {
                throw new NotFoundByException("%s. Could not update %s by id: %d", serviceName, entityName, entity.getId());
            }
        } catch (NotFoundByException ex) {
            log.warn("{}. Could not find {} by id: {}", serviceName, entityName, entity.getId());
            throw ex;
        } catch (Exception ex) {
            log.warn("{}. Could not update {}: {}", serviceName, entityName, entity.toString());
            throw new PersistException(ex.getMessage());
        }
    }
}
