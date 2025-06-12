package com.example.cormacarena.licenciamientoAmbiental;

import com.example.cormacarena.licenciamientoAmbiental.utils.SolicitudService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActualizarEstadoDePago implements JavaDelegate {

    private final SolicitudService solicitudService;
    private static final Logger logger = LoggerFactory.getLogger(ActualizarEstadoDePago.class);

    public ActualizarEstadoDePago(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        try {
            String idSolicitud = (String) execution.getVariable("codigoSolicitud");
            String estadoActual = (String) execution.getVariable("estado");

            logger.info("Delegate ejecutado - ID Solicitud: {}", idSolicitud);
            logger.info("Estado actual recibido: {}", estadoActual);

            if (idSolicitud != null && !idSolicitud.isBlank() && estadoActual != null) {
                String estadoNormalizado = estadoActual.trim();

                switch (estadoNormalizado.toLowerCase()) {
                    case "verificando pago":
                        solicitudService.actualizarEstado(idSolicitud, "Solicitud Pagada");
                        execution.setVariable("estado", "Solicitud Pagada");
                        logger.info("Estado actualizado a: Solicitud Pagada");
                        break;

                    case "verificando pago de licencia":
                        solicitudService.actualizarEstado(idSolicitud, "Licencia Pagada");
                        execution.setVariable("estado", "Licencia Pagada");
                        logger.info("Estado actualizado a: Licencia Pagada");
                        break;

                    default:
                        logger.warn("Estado actual no reconocido: {}", estadoActual);
                }
            } else {
                logger.warn("ID de solicitud o estado no proporcionado o inválido. idSolicitud={}, estado={}", idSolicitud, estadoActual);
            }

        } catch (Exception e) {
            logger.error("Error durante la ejecución del delegate de actualización de estado", e);
            throw e;
        }
    }
}
