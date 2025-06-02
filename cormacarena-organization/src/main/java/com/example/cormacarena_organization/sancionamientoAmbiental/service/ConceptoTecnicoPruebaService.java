package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoPruebasDTO;

public interface ConceptoTecnicoPruebaService {
    public ConceptoTecnicoPruebasDTO getProcessVariablesById(String processId);
    public String enviarFormulario(ConceptoTecnicoPruebasDTO conceptoTecnicoPruebasDTO, String processId);
}
