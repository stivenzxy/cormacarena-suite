package org.example.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "denunciasRadicadas")
public class DenunciasRadicadas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroRadicado;
    private Date fechaRadicacion;
    @OneToOne
    @PrimaryKeyJoinColumn
    private Denuncia denuncia;
    private boolean flagante;
}
