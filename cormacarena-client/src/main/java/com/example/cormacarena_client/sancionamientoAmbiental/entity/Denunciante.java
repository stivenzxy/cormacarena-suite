package com.example.cormacarena_client.sancionamientoAmbiental.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Denunciante")
public class Denunciante {

    @Id
    private long numeroIdentificacion;
    private String nombresDenunciante;
    private String apellidoDenunciante;
    private String tipoIdentificacionDenunciante;
    private String correoDenunciante;
}
