package com.example.cormacarena_client.sancionamientoAmbiental.service.base;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

public class BaseServiceImpl<T,ID> implements  BaseService<T,ID>{
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
