package com.example.cormacarena_client.solicitudPQRS.dto;

import lombok.Data;


@Data
public class FormularioDTO {
    private String tipoSolicitud;
    private String descripcion;
    private String nombre;
    private String email;
    private boolean adjDocumentos;
    private String nombreArchivo;
    private String fechaSolicitud;



    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdjDocumentos() {
        return adjDocumentos;
    }

    public void setAdjDocumentos(boolean adjDocumentos) {
        this.adjDocumentos = adjDocumentos;
    }

    public String getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(String fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
}
