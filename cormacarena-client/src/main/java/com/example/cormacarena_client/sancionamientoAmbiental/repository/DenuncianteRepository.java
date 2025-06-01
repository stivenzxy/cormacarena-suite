package com.example.cormacarena_client.sancionamientoAmbiental.repository;


import org.example.modelo.Denunciante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenuncianteRepository extends JpaRepository<Denunciante, Long> {
}