package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DeterminarInfraccionDTO;
public interface DeterminarInfraccionService {
    public DeterminarInfraccionDTO getProcessVariablesById(String processId);
    public String enviarFormulario(DeterminarInfraccionDTO determinarInfraccionDTO, String processId);
}
