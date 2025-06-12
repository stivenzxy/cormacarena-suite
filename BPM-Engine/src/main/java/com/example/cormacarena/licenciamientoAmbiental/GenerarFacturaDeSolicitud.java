package com.example.cormacarena.licenciamientoAmbiental;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;


@Component
public class GenerarFacturaDeSolicitud implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String sectorProyecto = (String) execution.getVariable("sectorProyecto");
        Number valorProyectoNum = (Number) execution.getVariable("valorProyecto");
        double valorProyecto = valorProyectoNum != null ? valorProyectoNum.doubleValue() : 0.0;

        double porcentaje;

        if ("hidrocarburos".equals(sectorProyecto)) {
            porcentaje = 0.03;
        } else if (valorProyecto > 500_000_000) {
            porcentaje = 0.03;
        } else {
            porcentaje = 0.01;
        }

        System.out.println(valorProyecto);
        System.out.println(sectorProyecto);

        double valorAPagar = valorProyecto * porcentaje;

        execution.setVariable("costoEvaluacion", valorAPagar);
    }

}


