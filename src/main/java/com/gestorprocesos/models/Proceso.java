package com.gestorprocesos.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Proceso {
    private final long pid;
    private final String nombre;
    private final int prioridad;
    private final int tiempoEjecucionOriginal;
    private final int memoriaSolicitada;
    private int memoriaAsignada;
    private int tiempoRestante;
    private EstadoProceso estado;
    private MotivoTerminacion motivoTerminacion;
    private final List<String> recursosSolicitados;
    private final List<String> recursosAsignados;
    private final List<MensajeProceso> mensajesRecibidos;

    public Proceso(long pid, String nombre, int prioridad, int tiempoEjecucion, int memoriaSolicitada, List<String> recursosSolicitados) {
        this.pid = pid;
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.tiempoEjecucionOriginal = tiempoEjecucion;
        this.memoriaSolicitada = memoriaSolicitada;
        this.memoriaAsignada = 0;
        this.tiempoRestante = tiempoEjecucion;
        this.estado = EstadoProceso.ESPERANDO;
        this.motivoTerminacion = null;
        this.recursosSolicitados = new ArrayList<>(recursosSolicitados == null ? List.of() : recursosSolicitados);
        this.recursosAsignados = new ArrayList<>();
        this.mensajesRecibidos = new ArrayList<>();
    }

    public long getPid() {
        return pid;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public int getTiempoEjecucionOriginal() {
        return tiempoEjecucionOriginal;
    }

    public int getMemoriaSolicitada() {
        return memoriaSolicitada;
    }

    public int getMemoriaAsignada() {
        return memoriaAsignada;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public EstadoProceso getEstado() {
        return estado;
    }

    public MotivoTerminacion getMotivoTerminacion() {
        return motivoTerminacion;
    }

    public List<String> getRecursosSolicitados() {
        return Collections.unmodifiableList(recursosSolicitados);
    }

    public List<String> getRecursosAsignados() {
        return Collections.unmodifiableList(recursosAsignados);
    }

    public List<MensajeProceso> getMensajesRecibidos() {
        return Collections.unmodifiableList(mensajesRecibidos);
    }

    public void cambiarEstado(EstadoProceso nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public void asignarRecursos(int memoria, List<String> recursos) {
        this.memoriaAsignada = memoria;
        this.recursosAsignados.clear();
        if (recursos != null) {
            this.recursosAsignados.addAll(recursos);
        }
    }

    public void liberarRecursos() {
        this.memoriaAsignada = 0;
        this.recursosAsignados.clear();
    }

    public void reducirTiempoRestante(int cantidad) {
        this.tiempoRestante = Math.max(0, this.tiempoRestante - Math.max(0, cantidad));
    }

    public boolean estaFinalizado() {
        return tiempoRestante <= 0;
    }

    public void establecerMotivoTerminacion(MotivoTerminacion motivoTerminacion) {
        this.motivoTerminacion = motivoTerminacion;
    }

    public void recibirMensaje(MensajeProceso mensaje) {
        this.mensajesRecibidos.add(mensaje);
    }

    @Override
    public String toString() {
        return "Proceso{" +
                "pid=" + pid +
                ", nombre='" + nombre + '\'' +
                ", estado=" + estado +
                ", prioridad=" + prioridad +
                ", tiempoRestante=" + tiempoRestante +
                ", memoriaAsignada=" + memoriaAsignada +
                ", recursosAsignados=" + recursosAsignados +
                ", motivoTerminacion=" + motivoTerminacion +
                '}';
    }
}
