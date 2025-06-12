package com.example.cormacarena_organization.licenciamientoAmbiental.DTO;

import lombok.Data;

@Data
public class SolicitudPreviewDTO {
    private String codigoSolicitud;
    private String nombreSolicitante;
    private String nombreProyecto;
    private String estado;
    private String profesionalAsignado;
    private String fechaVisitaTecnica;
    private String observacionesVisitaTecnica;
}
