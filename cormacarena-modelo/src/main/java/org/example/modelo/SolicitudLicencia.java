package org.example.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "solicitud_licencia")
public class SolicitudLicencia {

    @Id
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

    @Enumerated(EnumType.STRING)
    private SectorProyecto sectorProyecto;

    private Long valorProyecto;
    private String departamentoProyecto;
    private String municipioProyecto;
    private String nombreSoporteEIA;
    private LocalDate fechaSolicitud;

    private String estado;
}
