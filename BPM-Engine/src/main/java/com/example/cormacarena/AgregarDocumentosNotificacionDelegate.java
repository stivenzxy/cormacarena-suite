package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class AgregarDocumentosNotificacionDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {

        String nombreArchivo = (String) execution.getVariable("nombreArchivo");
        // Simular la obtención del nombre del archivo del anexo

        // Guardar el nombre del archivo en el contexto del proceso
        execution.setVariable("nombreArchivo", nombreArchivo);

        // Si necesitas concatenar con otros archivos o registrar logs, puedes hacerlo aquí
        System.out.println("Documento agregado a la notificación: " + nombreArchivo);
    }
}
