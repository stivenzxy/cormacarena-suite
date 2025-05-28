package com.example.cormacarena_client.sancionamientoAmbiental.DTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DenunciaDTO {
    private String nombresDenunciante;
    private String apellidosDenunciante;
    private String tipoIdentificacionDenunciante;
    private Long numeroIdentificacionDenunciante;
    private String correoDenunciante;
    private String descripcionDenuncia;
}

