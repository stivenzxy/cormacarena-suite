package com.example.cormacarena_client.licenciamientoAmbiental.service;

import com.example.cormacarena_client.licenciamientoAmbiental.DTO.SolicitudDTO;
import com.example.cormacarena_client.licenciamientoAmbiental.DTO.TaskInfo;
import org.example.modelo.SolicitudLicencia;

public interface LicenciaAmbientalService {
    String iniciarInstanciaProceso(SolicitudDTO solicitudLicenciaDTO);
    void actualizarVariablesProceso(String idProceso, SolicitudLicencia solicitudLicencia);
    void completeTask(String idProceso, String assignee);
    void setAssignee(String taskId, String userId);
    //TaskInfo getTaskInfoByProcessIdWithApi(String idProceso);
    TaskInfo getTaskInfoByProcessId(String idProceso);
    Object getVariableProceso(String idProceso, String variableName);
    void setProcessVariable(String processInstanceId, String variableName, Object value);
}
