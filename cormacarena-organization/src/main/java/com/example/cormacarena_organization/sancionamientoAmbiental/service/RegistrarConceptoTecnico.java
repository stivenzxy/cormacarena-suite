package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.InformeTecnicoDTO;

public interface RegistrarConceptoTecnico {
    public ConceptoTecnicoDTO getProcessVariablesById(String processId);
    public String enviarFormulario(ConceptoTecnicoDTO conceptoTecnicoDTO, String processId);
}
