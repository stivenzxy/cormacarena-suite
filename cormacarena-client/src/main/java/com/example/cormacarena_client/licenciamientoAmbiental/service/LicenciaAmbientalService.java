package com.example.cormacarena_client.licenciamientoAmbiental.service;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;

public interface LicenciaAmbientalService {
    String startProcessInstance(SolicitudDTO solicitudLicenciaDTO);
}
