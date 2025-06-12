package com.example.cormacarena_organization.licenciamientoAmbiental.service;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.TaskInfo;

import java.util.List;

public interface CoordinadorRevSolService {
    SolicitudDTO getProcessVariablesById(String idProceso);
    void setAssignee(String taskId, String userId);
    void updateTaskByProcessId(String processId, String taskId);
    TaskInfo getTaskInfoByProcessId(String idProceso);
    String getTaskIdByProcessIdWithApi(String idProceso);
    List<SolicitudPreviewDTO> obtenerSolicitudesVistaPrevia();
    void procesarSolicitud(String processId, String observacion, boolean esValida, String siguienteEstado, String nuevoResponsable);
    void asignarProfesional(String processId, String nombreProfesional, String siguienteEstado);
    void aprobarConceptoTecnico(String processId, String nuevoEstado);
}

