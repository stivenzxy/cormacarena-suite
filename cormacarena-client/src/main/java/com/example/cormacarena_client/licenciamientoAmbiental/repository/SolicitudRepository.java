package com.example.cormacarena_client.licenciamientoAmbiental.repository;

import jakarta.transaction.Transactional;
import org.example.modelo.SolicitudLicencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
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

    @Modifying
    @Transactional
    @Query("UPDATE SolicitudLicencia s SET s.estado = :nuevoEstado WHERE s.codigoSolicitud = :codigoSolicitud")
    int actualizarEstadoPorCodigoSolicitud(String codigoSolicitud, String nuevoEstado);
}

