package com.example.cormacarena_organization.sancionamientoAmbiental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ActoAdministrativo {
    @Id
    private String numeroRadicadoActoAdministrativo;
    private LocalDateTime fechaDeRadicacion;
    private String tipoActoAdministrativo;
    private String profesionalActoAdministrativo;
    private String profesionalRadicacionActoAdministrativo;
    private String descripcionActoAdministrativo;
    private String DescripcionResolucion;
    private Long idDenuncia;

}
