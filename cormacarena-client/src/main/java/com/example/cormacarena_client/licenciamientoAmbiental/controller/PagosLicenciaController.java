package com.example.cormacarena_client.licenciamientoAmbiental.controller;

import com.example.cormacarena_client.licenciamientoAmbiental.service.LicenciaAmbientalService;
import com.example.cormacarena_client.licenciamientoAmbiental.service.SolicitudLicenciaService;
import lombok.RequiredArgsConstructor;
import org.example.modelo.SolicitudLicencia;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PagosLicenciaController {

    private final SolicitudLicenciaService solicitudLicenciaService;
    private final LicenciaAmbientalService licenciaAmbientalService;

    @GetMapping("/pagos-licenciamiento")
    public String mostrarPagosPorSolicitante(@RequestParam(name = "idSolicitante", required = false) String idSolicitante,
                                             Model model) {
        if (idSolicitante != null && !idSolicitante.isBlank()) {
            List<SolicitudLicencia> solicitudesFiltradas = solicitudLicenciaService
                    .obtenerPorIdSolicitante(idSolicitante)
                    .stream()
                    .filter(solicitud -> "PAGAR_EVALUACION".equalsIgnoreCase(solicitud.getEstado()) ||
                            "Pagar Licencia".equalsIgnoreCase(solicitud.getEstado()))
                    .toList();

            Map<String, Object> costoEvaluacion = new HashMap<>();
            Map<String, Object> costoLicencia = new HashMap<>();
            for (SolicitudLicencia solicitud : solicitudesFiltradas) {
                Object costoEvaluacionFiltrado = licenciaAmbientalService.getVariableProceso(solicitud.getCodigoSolicitud(), "costoEvaluacion");
                Object costoLicenciaFiltrado = licenciaAmbientalService.getVariableProceso(solicitud.getCodigoSolicitud(), "valorLicencia");
                costoEvaluacion.put(solicitud.getCodigoSolicitud(), costoEvaluacionFiltrado);
                costoLicencia.put(solicitud.getCodigoSolicitud(), costoLicenciaFiltrado);
            }

            model.addAttribute("solicitudes", solicitudesFiltradas);
            model.addAttribute("costoEvaluacion", costoEvaluacion);
            model.addAttribute("costoLicencia", costoLicencia);
            model.addAttribute("idSolicitante", idSolicitante);
        }

        return "licenciamiento/listadoPagosEvaluacion";
    }

    @PostMapping("/realizar-pago/{codigo}")
    public String procesarPago(@PathVariable(name = "codigo") String idProceso,
                               @RequestParam(name = "valorIngresado") Double pagoIngresado,
                               @RequestParam(name = "idSolicitante") String idSolicitante) {

        solicitudLicenciaService.actualizarEstadoSolicitud(idProceso, "Verificando Pago");
        licenciaAmbientalService.setProcessVariable(idProceso, "estado", "Verificando Pago");
        licenciaAmbientalService.setProcessVariable(idProceso, "pagoIngresado", pagoIngresado);
        licenciaAmbientalService.completeTask(idProceso,"CoordinadorDeGrupo");

        return "redirect:/listado-solicitudes?idSolicitante=" + idSolicitante;
    }

    @PostMapping("realizar-pago-licencia/{codigo}")
    public String pagarLicencia(@PathVariable(name = "codigo") String idProceso,
                                @RequestParam(name = "valorIngresado") Double pagoIngresado,
                                @RequestParam(name = "idSolicitante") String idSolicitante) {

        solicitudLicenciaService.actualizarEstadoSolicitud(idProceso, "Verificando Pago de Licencia");
        licenciaAmbientalService.setProcessVariable(idProceso, "estado","Verificando Pago de Licencia");
        licenciaAmbientalService.setProcessVariable(idProceso, "pagoIngresadoLicencia", pagoIngresado);
        licenciaAmbientalService.completeTask(idProceso,"CoordinadorDeGrupo");

        return "redirect:/listado-solicitudes?idSolicitante=" + idSolicitante;
    }
}
