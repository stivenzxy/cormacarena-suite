package com.example.cormacarena.licenciamientoAmbiental.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class SolicitudService {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudService.class);

    @Value("${DB_URL}")
    private String dbUrl;

    @Value("${DB_USER}")
    private String dbUser;

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    public void actualizarEstado(String codigoSolicitud, String nuevoEstado) {
        String sql = "UPDATE solicitud_licencia SET estado = ? WHERE codigo_solicitud = ?";

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setString(2, codigoSolicitud);
            int filas = stmt.executeUpdate();

            logger.info("Solicitud con código {} actualizada a '{}'. Filas afectadas: {}", codigoSolicitud, nuevoEstado, filas);

        } catch (SQLException e) {
            logger.error("Error al actualizar la solicitud con código {}", codigoSolicitud, e);
            throw new RuntimeException("Error al actualizar el estado de la solicitud", e);
        }
    }
}
