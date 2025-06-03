package org.example.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
