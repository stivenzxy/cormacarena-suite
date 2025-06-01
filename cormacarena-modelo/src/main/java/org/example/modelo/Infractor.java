package org.example.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Infractor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInfractor;
    private String nombresInfractor;
    private String apellidosInfractor;
    private String tipoDocumentoInfractor;
    private Long numeroDocumentoInfractor;
    @OneToMany(mappedBy = "infractor", cascade = CascadeType.ALL)
    private List<Sancion> sanciones;

}
