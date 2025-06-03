package com.example.cormacarena_client.licenciamientoAmbiental.repository;

import org.example.modelo.SolicitudLicencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudLicencia, String> {
    List<SolicitudLicencia> findByIdSolicitante(String idSolicitante);
    Optional<SolicitudLicencia> findFirstByEstado(String estado);
    Optional<SolicitudLicencia> findByEstadoAndIdSolicitante(String idSolicitante, String estado);
    boolean existsByCodigoSolicitud(String codigoSolicitud);
    SolicitudLicencia findByCodigoSolicitud(String codigoSolicitud);
}

