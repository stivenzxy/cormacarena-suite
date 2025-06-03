package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
public class RadicacionSolicitud implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Generar número de radicado
        String numeroRadicado = "RAD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Formatear fecha (ej. "27/05/2025")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaRadicacion = LocalDateTime.now().format(formatter);

        // Asignar variables al proceso
        execution.setVariable("numeroRadicado", numeroRadicado);
        execution.setVariable("fechaRadicacion", fechaRadicacion);
        execution.setVariable("estado", "Radicado");

        System.out.println("PQRS radicada automáticamente con número: " + numeroRadicado +
                " el día " + fechaRadicacion);
    }
}
