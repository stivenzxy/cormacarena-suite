package com.example.cormacarena_organization.licenciamientoAmbiental.service;

import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.SolicitudPreviewDTO;
import com.example.cormacarena_organization.licenciamientoAmbiental.DTO.TaskInfo;

import java.util.List;

public interface CoordinadorRevSolService {
    List<String> obtenerProcesosPorIdActividad(String idActividad);
    SolicitudDTO getProcessVariablesById(String idProceso);
    void setAssignee(String taskId, String userId);
    TaskInfo getTaskInfoByProcessId(String idProceso);
    String getTaskIdByProcessIdWithApi(String idProceso);
    List<SolicitudPreviewDTO> obtenerSolicitudesVistaPrevia();
    String aprobarSolicitud(String processId);
    String rechazarSolicitud(String processId);
    void actualizarEstadoProceso(String processId, String estado);
}
