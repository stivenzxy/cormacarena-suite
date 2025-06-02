package com.example.cormacarena_client.licenciamientoAmbiental.service;


import org.example.modelo.SolicitudLicencia;

import java.util.List;

public interface SolicitudLicenciaService {
    List<SolicitudLicencia> obtenerTodasLasSolicitudes();
    void crearNuevaSolicitud(SolicitudLicencia solicitudLicencia);
    SolicitudLicencia obtenerPorIdSolicitante(String idSolicitante);
}
