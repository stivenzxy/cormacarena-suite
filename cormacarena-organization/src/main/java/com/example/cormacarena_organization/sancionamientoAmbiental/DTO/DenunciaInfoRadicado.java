package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;
@Data
public class DenunciaInfoRadicado {
    private String nombresDenunciante;
    private String apellidosDenunciante;
    private String correoDenunciante;
    private String descripcionDenuncia;
    private Long idDenuncia;
    private TaskInfo taskInfo;
    public void setTaskInfo(TaskInfo taskInfo){
        this.taskInfo=taskInfo;
    }
    public String getNombresDenunciante() {
        return nombresDenunciante;
    }
    public String getDescripcionDenuncia() {
        return descripcionDenuncia;
    }
    public Long getIdDenuncia(){
        return idDenuncia;
    }

}
