package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class NotificarCargoPresuntoInfractor implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Obtener variables del proceso si quieres
        String usuario = (String) execution.getVariable("nombreInfractor");
        String DescripcionFormulacionCargos = (String) execution.getVariable("DescripcionFormulacionCargos");


        // Simular notificación (print en consola)
        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Descripción de los cargos: " + DescripcionFormulacionCargos);

    }

}
