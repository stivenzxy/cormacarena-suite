package com.example.cormacarena_organization.sancionamientoAmbiental.service.base;

import java.util.List;

public interface BaseServiceOrganization<T,ID> {
    List<T> findAll();

    T findById(ID id);

    void save(T entity);
}