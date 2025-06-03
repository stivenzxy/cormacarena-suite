package org.example.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "sancionamiento_ambiental")
public class SancionamientoAmbiental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String processId;

    @Column(name = "status")
    private String status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "denunciasRadicadas_id")
    private DenunciasRadicadas denunciasRadicadas;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Denuncia denuncia;
}
