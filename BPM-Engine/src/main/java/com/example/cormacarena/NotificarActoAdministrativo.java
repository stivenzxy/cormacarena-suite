package com.example.cormacarena;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class NotificarActoAdministrativo implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String usuario = (String) execution.getVariable("nombreInfractor");
        String descripcionActoAdministrativo = (String) execution.getVariable("descripcionActoAdministrativo");
        String profesionalActoAdministrativo = (String) execution.getVariable("profesionalActoAdministrativo");


        // Simular notificación (print en consola)
        System.out.println("NOTIFICACIÓN para usuario: " + usuario);
        System.out.println("Profesional del acto administrativo: " + profesionalActoAdministrativo);
        System.out.println("Descripción del acto administrativo: " + descripcionActoAdministrativo);


    }
}
