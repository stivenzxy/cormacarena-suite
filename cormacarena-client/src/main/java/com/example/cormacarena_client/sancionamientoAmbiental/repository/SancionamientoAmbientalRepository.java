package com.example.cormacarena_client.sancionamientoAmbiental.repository;

import com.example.cormacarena_client.sancionamientoAmbiental.entity.SancionamientoAmbiental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SancionamientoAmbientalRepository extends JpaRepository<SancionamientoAmbiental,Long> {
    SancionamientoAmbiental findSancionamientoAmbientalByProcessId(String processId);
}
