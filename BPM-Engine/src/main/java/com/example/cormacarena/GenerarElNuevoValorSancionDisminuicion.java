package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class GenerarElNuevoValorSancionDisminuicion implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String usuario = (String) execution.getVariable("nombreInfractor");
        Long valorInfraccionAntes = (Long) execution.getVariable("valorInfraccion");
        Long porcentajeDisminuicion = (Long) execution.getVariable("porcentajeDisminuicion");


        Long valorInfraccionActual = valorInfraccionAntes - valorInfraccionAntes*(porcentajeDisminuicion/100);



        // Simular notificación (print en consola)
        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Valor infracción anterior: " + valorInfraccionAntes);
        System.out.println("Valor infracción actual: " + valorInfraccionActual);

        execution.setVariable("valorInfraccion",valorInfraccionActual);
    }
}
