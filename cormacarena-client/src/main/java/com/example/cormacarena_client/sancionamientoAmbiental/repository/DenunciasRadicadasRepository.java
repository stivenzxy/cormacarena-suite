package com.example.cormacarena_client.sancionamientoAmbiental.repository;

import org.example.modelo.DenunciasRadicadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciasRadicadasRepository extends JpaRepository<DenunciasRadicadas,Long> {
}
