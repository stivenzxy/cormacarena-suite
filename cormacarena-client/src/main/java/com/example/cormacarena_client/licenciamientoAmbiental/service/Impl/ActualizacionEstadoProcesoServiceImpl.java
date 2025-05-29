package com.example.cormacarena_client.licenciamientoAmbiental.service.Impl;

import com.example.cormacarena_client.licenciamientoAmbiental.entity.SolicitudLicencia;
import com.example.cormacarena_client.licenciamientoAmbiental.repository.SolicitudRepository;
import com.example.cormacarena_client.licenciamientoAmbiental.service.ActualizacionEstadoProceso;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActualizacionEstadoProcesoServiceImpl implements ActualizacionEstadoProceso {

    private final SolicitudRepository solicitudRepository;

    @Override
    @Transactional
    public void updateReviewAndStatus(String processId, String status) {
        SolicitudLicencia solicitudLicencia = solicitudRepository.findByCodigoSolicitud(processId);

        if (solicitudLicencia == null) {
            throw new EntityNotFoundException("No se encontró solicitud con ID: " + processId);
        }

        solicitudLicencia.setEstado(status);
        solicitudRepository.save(solicitudLicencia);
        System.out.println("Status updated via JPA.");
    }
}
