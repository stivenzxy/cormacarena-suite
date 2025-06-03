package org.example.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
