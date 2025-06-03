package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class NotifyUserDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Obtener variables del proceso si quieres
        String usuario = (String) execution.getVariable("nombreInfractor");
        String tipoInfraccion = (String) execution.getVariable("tipoInfraccion");
        Long valorInfracion = (Long) execution.getVariable("sancionTotal");
        String descripcionConclusiones = (String) execution.getVariable("descripcionConclusiones");

        // Simular notificación (print en consola)
        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Tipo de infraccion: " + tipoInfraccion);
        System.out.println("Valor de la infraccion: " + valorInfracion);
        System.out.println("descripcion de la infraccion: " + descripcionConclusiones);
    }
}