package com.example.cormacarena_organization.sancionamientoAmbiental.service.base;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public class BaseServiceImpl<T,ID> implements BaseServiceOrganization<T,ID> {
    protected final JpaRepository<T,ID> repository;
    public BaseServiceImpl(JpaRepository<T,ID> repository){
        this.repository=repository;
    }
    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la entidad con ID: " + id));
    }

    @Override
    public void save(T entity) {
        repository.save(entity);
    }
}
