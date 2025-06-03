package com.example.cormacarena_client.licenciamientoAmbiental.service;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.DTO.TaskInfo;
import org.example.modelo.SolicitudLicencia;

public interface LicenciaAmbientalService {
    String iniciarInstanciaProceso(SolicitudDTO solicitudLicenciaDTO);
    String actualizarVariablesProceso(String idProceso, SolicitudLicencia solicitudLicencia);
    String completeTask(String idProceso);
    void setAssignee(String taskId, String userId);
    //TaskInfo getTaskInfoByProcessIdWithApi(String idProceso);
    TaskInfo getTaskInfoByProcessId(String idProceso);
}
