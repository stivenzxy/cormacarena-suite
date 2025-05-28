package com.example.cormacarena_client.sancionamientoAmbiental.service;

import com.example.cormacarena_client.sancionamientoAmbiental.DTO.TaskInfo;
import com.example.cormacarena_client.sancionamientoAmbiental.DTO.DenunciaDTO;
import com.example.cormacarena_client.sancionamientoAmbiental.entity.SancionamientoAmbiental;

import java.sql.SQLException;


public interface DenuncianteRealizaDenunciaService {

    String startProcessInstance(DenunciaDTO denunciaDTO, String ruta);
    void setAssignee(String taskId, String userId);
    TaskInfo getTaskInfoByProcessId(String processId);

    TaskInfo getTaskInfoByProcessIdWithApi(String processId);
    String updateProcessVariables(String processId, DenunciaDTO denunciaDTO, String ruta);
    String completeTask(String processId, DenunciaDTO denunciaDTO);
    void messageEvent(String processId);

    void updateReviewAndStatus(String processId, String status) throws SQLException;

}
