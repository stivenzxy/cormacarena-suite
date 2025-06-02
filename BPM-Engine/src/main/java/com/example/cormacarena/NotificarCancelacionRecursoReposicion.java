package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class NotificarCancelacionRecursoReposicion implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String usuario = (String) execution.getVariable("nombreInfractor");
        String mensajeCancelacion = "No se puede dar los recursos de reposición";


        // Simular notificación (print en consola)
        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Mensajes: " + mensajeCancelacion);
    }
}
