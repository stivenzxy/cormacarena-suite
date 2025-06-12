package com.example.cormacarena_organization.licenciamientoAmbiental.service;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.*;

import java.util.List;

public interface ProfesionalRevSolService {
    SolicitudDTO getProcessVariablesById(String idProceso);
    List<SolicitudPreviewDTO> obtenerSolicitudesVistaPrevia();
    TaskInfo getTaskInfoByProcessId(String idProceso);
    void realizarVisitaTecnica(String processId, VisitaTecnicaDTO visitaDTO, String nuevoEstado);
    String getTaskIdByProcessIdWithApi(String idProceso);
    void updateTaskByProcessId(String processId, String taskId);
    void setAssignee(String taskId, String userId);
    void emitirConceptoTecnico(String processId, String fechaConceptoTecnico, String nuevoEstado);
}
