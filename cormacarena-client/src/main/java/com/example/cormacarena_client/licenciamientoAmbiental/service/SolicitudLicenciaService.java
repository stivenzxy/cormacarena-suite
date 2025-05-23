package com.example.cormacarena_client.licenciamientoAmbiental.service;

import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;

import java.util.List;

public interface SolicitudLicenciaService {
    List<SolicitudLicencia> obtenerTodasLasSolicitudes();
    void crearNuevaSolicitud(SolicitudLicencia solicitudLicencia);
}
