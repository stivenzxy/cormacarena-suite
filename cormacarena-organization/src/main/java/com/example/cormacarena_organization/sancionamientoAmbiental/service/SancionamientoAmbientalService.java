package com.example.cormacarena_organization.sancionamientoAmbiental.service;


import org.example.modelo.SancionamientoAmbiental;

public interface SancionamientoAmbientalService {

    SancionamientoAmbiental encontrarSancionamientoAmbientalPorProcessId(String processId);

    void actualizarSancionAmbiental(Long id, SancionamientoAmbiental sancionamientoAmbiental);

}
