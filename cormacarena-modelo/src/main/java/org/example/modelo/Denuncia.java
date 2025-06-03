package org.example.modelo;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "denuncia")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDenuncia;
    private String descripcionDenuncia;
    private String soporteDenuncia;
    @ManyToOne
    @JoinColumn(name = "denunciante_id", referencedColumnName = "numeroIdentificacion")
    private Denunciante denunciante;
    private String estado;
    @OneToOne
    @PrimaryKeyJoinColumn
    private DenunciasRadicadas denunciaRadicada;
}
