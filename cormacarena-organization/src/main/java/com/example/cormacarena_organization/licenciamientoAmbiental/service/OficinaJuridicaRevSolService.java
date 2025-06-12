package com.example.cormacarena_organization.licenciamientoAmbiental.service;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.TaskInfo;

import java.util.List;

public interface OficinaJuridicaRevSolService {
    void emitirResolucionAdministrativa(String processId, String fechaResolucionJuridica, String descripcionJuridica,String nuevoEstado);
}
