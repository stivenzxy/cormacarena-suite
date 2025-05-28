package com.example.cormacarena_client.sancionamientoAmbiental.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DenunciaInfoRadicado {
    private Long idDenuncia;
    private String nombresDenunciante;
    private String apellidosDenunciante;
    private String correoDenunciante;
    private String descripcionDenuncia;
}
