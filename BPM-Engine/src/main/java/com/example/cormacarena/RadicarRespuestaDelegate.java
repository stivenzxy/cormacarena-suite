package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class RadicarRespuestaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Obtener variables necesarias
        String jefe = (String) execution.getVariable("jefeDependencia");
        String documento = (String) execution.getVariable("documentoResponsable");
        String firma = (String) execution.getVariable("firma");

        // Simulación de lógica de radicación
        String numeroRadicadoRespuesta = "RESP-" + System.currentTimeMillis();

        // Guardar el radicado como variable del proceso
        execution.setVariable("numeroRadicadoRespuesta", numeroRadicadoRespuesta);

        // También podrías persistir en una base de datos o enviar a otro sistema aquí
        System.out.printf("Respuesta radicada por %s. Documento: %s. Firma: %s. Número: %s%n",
                jefe, documento, firma, numeroRadicadoRespuesta);
    }
}
