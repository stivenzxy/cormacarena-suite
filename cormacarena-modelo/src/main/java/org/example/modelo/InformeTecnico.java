package org.example.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class InformeTecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInformeTecnico;
    private LocalDateTime fechaCreacionInformeTecnico;
    private LocalDateTime fechaVisitaTecnica;
    private String encargadoVisitaTecnica;
    private String descripcionVisitaTecnica;
    private String razonesNoCometerDelito;
    @OneToOne
    @PrimaryKeyJoinColumn
    private Sancion sancion;

}
