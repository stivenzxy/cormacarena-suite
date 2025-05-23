package com.example.cormacarena_client.licenciamientoAmbiental.repository;

import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudLicencia, Long> {
}
