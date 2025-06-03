package com.example.cormacarena_organization.sancionamientoAmbiental.service;

import com.example.cormacarena_organization.sancionamientoAmbiental.DTO.ActoAdministrativaDTO;

public interface ResolucionActoAdministrativo {
    public ActoAdministrativaDTO getProcessVariablesById(String processId);
    public String enviarFormulario(ActoAdministrativaDTO actoAdministrativaDTO, String processId);
}
