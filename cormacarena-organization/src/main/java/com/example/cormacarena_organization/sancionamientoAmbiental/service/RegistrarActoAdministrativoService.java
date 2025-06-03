package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ActoAdministrativaDTO;
import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ConceptoTecnicoDTO;

public interface RegistrarActoAdministrativoService {
    public ActoAdministrativaDTO getProcessVariablesById(String processId);
    public String enviarFormulario(ActoAdministrativaDTO actoAdministrativaDTO, String processId);
}
