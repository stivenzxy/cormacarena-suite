package com.example.cormacarena_client.sancionamientoAmbiental.repository;


import org.example.modelo.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
}
