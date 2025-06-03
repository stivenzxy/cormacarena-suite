package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ExonerarInsfractor implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String usuario = (String) execution.getVariable("nombreInfractor");
        String descripcionActoAdministrativo = (String) execution.getVariable("descripcionActoAdministrativo");
        Long valorInfraccionAntes = (Long) execution.getVariable("valorInfraccion");
        Long valorInfraccionActual = Long.valueOf(0);



        // Simular notificación (print en consola)
        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Valor infracción anterior: " + valorInfraccionAntes);
        System.out.println("Valor infracción actual: " + valorInfraccionActual);

        execution.setVariable("valorInfraccion",valorInfraccionActual);
    }
}
