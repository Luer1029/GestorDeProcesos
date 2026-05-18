package com.gestorprocesos.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MensajeProceso {
    private final long origenPid;
    private final long destinoPid;
    private final String contenido;
    private final LocalDateTime fechaHora;

    public MensajeProceso(long origenPid, long destinoPid, String contenido) {
        this.origenPid = origenPid;
        this.destinoPid = destinoPid;
        this.contenido = contenido;
        this.fechaHora = LocalDateTime.now();
    }

    public long getOrigenPid() {
        return origenPid;
    }

    public long getDestinoPid() {
        return destinoPid;
    }

    public String getContenido() {
        return contenido;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "Mensaje{origen=" + origenPid + ", destino=" + destinoPid + ", contenido='" + contenido + '\'' + ", fechaHora=" + fechaHora.format(formatter) + '}';
    }
}
