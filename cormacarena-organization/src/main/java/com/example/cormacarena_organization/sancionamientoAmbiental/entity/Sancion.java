package com.example.cormacarena_organization.sancionamientoAmbiental.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Sancion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSancion;
    private String tipoSancion;
    private String estadoSancion;
    @ManyToOne
    @PrimaryKeyJoinColumn
    private Infractor infractor;
    private Long valorSancion;
    private String resolucionSancion;
}
