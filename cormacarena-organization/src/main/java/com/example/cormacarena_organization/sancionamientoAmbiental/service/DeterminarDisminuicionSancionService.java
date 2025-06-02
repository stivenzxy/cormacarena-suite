package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoPruebasDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.DisminuicionSancionDTO;

public interface DeterminarDisminuicionSancionService {
    public DisminuicionSancionDTO getProcessVariablesById(String processId);
    public String enviarFormulario(DisminuicionSancionDTO disminuicionSancionDTO, String processId);
}
