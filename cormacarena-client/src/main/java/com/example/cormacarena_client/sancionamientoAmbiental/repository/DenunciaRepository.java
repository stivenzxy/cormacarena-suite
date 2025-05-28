package com.example.cormacarena_client.sancionamientoAmbiental.repository;

import com.example.cormacarena_client.sancionamientoAmbiental.entity.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciaRepository extends JpaRepository<Denuncia, Long> {
}
