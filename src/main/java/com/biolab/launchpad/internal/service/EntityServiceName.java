package com.biolab.launchpad.internal.service;

import com.biolab.launchpad.internal.repository.model.Name;
import com.biolab.launchpad.internal.security.exceptions.NotFoundByException;
import com.biolab.launchpad.internal.security.exceptions.PersistException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Log4j2
public class EntityServiceName<T extends Name> {

    protected final CrudRepository<T, String> repository;

    public T create(T entity) {
        String serviceName = getClass().getSimpleName();
        String entityName = entity.getClass().getSimpleName();
        try {

            if (repository.existsById(String.valueOf(entity.getName()))) {
                throw new NotFoundByException("%s. already exist %s by id: %d", serviceName, entityName, repository.existsById(String.valueOf(entity.getName())));
            } else {
                entity.markAsNew(true);
                return repository.save(entity);
            }

        } catch (Exception ex) {
            log.warn("{}. Could not create {}: {}", serviceName, entityName, entity.toString());
            throw new PersistException(ex.getMessage());
        }
    }

    public List<T> findAll() {
        return (List<T>) repository.findAll();
    }

    public Optional<T> findById(String id) {
        String serviceName = getClass().getSimpleName();
        try {
            return repository.findById(id);
        } catch (Exception ex) {
            log.warn("{}. Could not find by id: {}", serviceName, id);
            throw new PersistException(ex.getMessage());
        }
    }

    public void deleteById(String id) {
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
            if (repository.existsById(String.valueOf(entity.getName()))) {
                entity.markAsNew(false);
                return repository.save(entity);
            } else {
                throw new NotFoundByException("%s. Could not update %s by id: %d", serviceName, entityName, repository.existsById(String.valueOf(entity.getName())));
            }
        } catch (NotFoundByException ex) {
            log.warn("{}. Could not find {} by id: {}", serviceName, entityName, repository.existsById(String.valueOf(entity.getName())));
            throw ex;
        } catch (Exception ex) {
            log.warn("{}. Could not update {}: {}", serviceName, entityName, entity.toString());
            throw new PersistException(ex.getMessage());
        }
    }
}
