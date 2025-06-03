package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DenunciaProcesoVerificacionDTO;

public interface VerificacionHechosDenuncia {
    public DenunciaProcesoVerificacionDTO getProcessVariablesById(String processId);
    public String enviarFormulario(DenunciaProcesoVerificacionDTO denunciaProcesoVerificacionDTO, String processId);
}
