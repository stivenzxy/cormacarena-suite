package com.example.cormacarena_organization.licenciamientoAmbiental.DTO;

import com.example.cormacarena_organization.licenciamientoAmbiental.enums.SectorProyecto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SolicitudDTO {

    // Id del proceso
    private String codigoSolicitud;
    // Datos del Solicitante
    private String nombreSolicitante;
    private String tipoIdentificacion;
    private String idSolicitante;
    private String telefono;
    private String email;
    private String direccionResidencia;

    // Datos del Proyecto
    private String nombreProyecto;
    private SectorProyecto sectorProyecto;
    private Long valorProyecto;
    private String departamentoProyecto;
    private String municipioProyecto;

    private String nombreSoporteEIA;
    private MultipartFile soporteEIAPdf;

    private String estado;
    private String profesionalAsignado;
    private String fechaVisitaTecnica;
    private String observacionesVisitaTecnica;
    private String fechaConceptoTecnico;
    private String fechaResolucionJuridica;
    private String descripcionResolucionJuridica;
}
