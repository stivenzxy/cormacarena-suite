package com.example.cormacarena_client.licenciamientoAmbiental.service;


import org.example.modelo.SolicitudLicencia;

import java.util.List;

public interface SolicitudLicenciaService {
    List<SolicitudLicencia> obtenerTodasLasSolicitudes();
    List<SolicitudLicencia> obtenerPorIdSolicitante(String idSolicitante);
    void crearNuevaSolicitud(SolicitudLicencia solicitudLicencia);
    SolicitudLicencia obtenerPorEstadoYSolicitante(String idSolicitante, String estado);
    void actualizarSolicitudExistente(String codigoSolicitud,SolicitudLicencia solicitudLicencia);
    void actualizarEstadoSolicitud(String codigoSolicitud, String estado);
}
