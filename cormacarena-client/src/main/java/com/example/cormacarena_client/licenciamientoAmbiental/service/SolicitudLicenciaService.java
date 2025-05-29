package com.example.cormacarena_client.licenciamientoAmbiental.service;

import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;

import java.util.List;
import java.util.Optional;

public interface SolicitudLicenciaService {
    List<SolicitudLicencia> obtenerTodasLasSolicitudes();
    List<SolicitudLicencia> obtenerPorIdSolicitante(String idSolicitante);
    void crearNuevaSolicitud(SolicitudLicencia solicitudLicencia);
    SolicitudLicencia obtenerPorEstadoYSolicitante(String idSolicitante, String estado);
    void actualizarSolicitudExistente(String codigoSolicitud,SolicitudLicencia solicitudLicencia);
}
