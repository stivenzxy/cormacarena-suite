package com.example.cormacarena_organization.licenciamientoAmbiental.service;

import java.sql.SQLException;

public interface ActualizarEstadoService {
    void actualizarEstadoDatabase(String processId, String status) throws SQLException;
    void actualizarEstadoProceso(String processId, String estado);
}
