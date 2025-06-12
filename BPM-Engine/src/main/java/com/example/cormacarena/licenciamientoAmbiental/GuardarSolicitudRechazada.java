package com.example.cormacarena.licenciamientoAmbiental;

import com.example.cormacarena.licenciamientoAmbiental.utils.SolicitudService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GuardarSolicitudRechazada implements JavaDelegate {

    private final SolicitudService solicitudService;
    private static final Logger logger = LoggerFactory.getLogger(GuardarSolicitudRechazada.class);

    public GuardarSolicitudRechazada(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String idSolicitud = (String) execution.getVariable("codigoSolicitud");
        System.out.println(idSolicitud);
        if (idSolicitud != null && !idSolicitud.isBlank()) {
            solicitudService.actualizarEstado(idSolicitud, "RECHAZADO");
        } else {
            logger.warn("ID de solicitud no proporcionado o inválido.");
        }
    }
}
