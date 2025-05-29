package com.example.cormacarena_client.sancionamientoAmbiental.repository;

import com.example.cormacarena_client.sancionamientoAmbiental.entity.DenunciasRadicadas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciasRadicadasRepository extends JpaRepository<DenunciasRadicadas,Long> {
}
