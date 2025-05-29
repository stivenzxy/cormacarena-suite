package com.example.cormacarena_client.sancionamientoAmbiental.service.base;

import java.util.List;

public interface BaseService <T,ID> {
    List<T> findAll();

    T findById(ID id);

    void save(T entity);
}