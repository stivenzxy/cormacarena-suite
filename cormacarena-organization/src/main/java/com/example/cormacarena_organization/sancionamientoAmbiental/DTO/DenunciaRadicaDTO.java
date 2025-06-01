package com.example.cormacarena_organization.sancionamientoAmbiental.DTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class DenunciaRadicaDTO {
    LocalDate fechaRadicacion;
    String encargadoDenuncia;
    String flagrancia;


}
