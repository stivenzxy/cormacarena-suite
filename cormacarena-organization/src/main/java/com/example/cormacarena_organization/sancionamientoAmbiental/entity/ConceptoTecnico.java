package com.example.cormacarena_organization.sancionamientoAmbiental.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ConceptoTecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idConceptoTecnico;
    private LocalDateTime fechaConceptoTecnico;
    private LocalDateTime fechaVisitaTecnica;
    private String encargadoVisitaTecnica;
    private String descripcionVisitaTecnica;
    private String razonesInfraccion;
    @OneToOne
    @PrimaryKeyJoinColumn
    private Sancion sancion;
}
