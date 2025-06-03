package com.example.cormacarena_client.sancionamientoAmbiental.repository;

import org.example.modelo.SancionamientoAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SancionamientoAmbientalRepository extends JpaRepository<SancionamientoAmbiental,Long> {
    SancionamientoAmbiental findSancionamientoAmbientalByProcessId(String processId);
}
