package com.gestorprocesos.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gestorprocesos.models.Proceso;

public class ResourceManager {
    private final int cpuTotal;
    private final int memoriaTotal;
    private int cpuUsada;
    private int memoriaUsada;
    private long pidEnCPU;
    private final Set<String> recursosOcupados;
    private final Map<Long, List<String>> recursosPorProceso;
    private final Map<Long, Integer> memoriaPorProceso;
    private String ultimoMensaje;

    public ResourceManager(int cpuTotal, int memoriaTotal) {
        this.cpuTotal = Math.max(1, cpuTotal);
        this.memoriaTotal = Math.max(1, memoriaTotal);
        this.cpuUsada = 0;
        this.memoriaUsada = 0;
        this.pidEnCPU = -1;
        this.recursosOcupados = new HashSet<>();
        this.recursosPorProceso = new HashMap<>();
        this.memoriaPorProceso = new HashMap<>();
        this.ultimoMensaje = "Sistema inicializado.";
    }

    public boolean solicitarRecursos(Proceso proceso) {
        int memoriaSolicitada = proceso.getMemoriaSolicitada();
        List<String> recursosSolicitados = proceso.getRecursosSolicitados();

        if (memoriaUsada + memoriaSolicitada > memoriaTotal) {
            ultimoMensaje = "No hay memoria suficiente para el proceso PID " + proceso.getPid() + ".";
            return false;
        }

        for (String recurso : recursosSolicitados) {
            if (recursosOcupados.contains(recurso)) {
                ultimoMensaje = "Conflicto de recursos: el recurso '" + recurso + "' ya está ocupado.";
                return false;
            }
        }

        memoriaUsada += memoriaSolicitada;
        memoriaPorProceso.put(proceso.getPid(), memoriaSolicitada);
        recursosPorProceso.put(proceso.getPid(), new ArrayList<>(recursosSolicitados));
        recursosOcupados.addAll(recursosSolicitados);
        proceso.asignarRecursos(memoriaSolicitada, recursosSolicitados);
        ultimoMensaje = "Recursos asignados al proceso PID " + proceso.getPid() + ".";
        return true;
    }

    public boolean solicitarCPU(Proceso proceso) {
        if (cpuUsada >= cpuTotal) {
            ultimoMensaje = "CPU ocupada. El proceso PID " + proceso.getPid() + " debe esperar.";
            return false;
        }

        cpuUsada = 1;
        pidEnCPU = proceso.getPid();
        ultimoMensaje = "CPU asignada al proceso PID " + proceso.getPid() + ".";
        return true;
    }

    public void liberarCPU(Proceso proceso) {
        if (pidEnCPU == proceso.getPid()) {
            cpuUsada = 0;
            pidEnCPU = -1;
            ultimoMensaje = "CPU liberada por el proceso PID " + proceso.getPid() + ".";
        }
    }

    public void liberarRecursos(Proceso proceso) {
        Integer memoriaAsignada = memoriaPorProceso.remove(proceso.getPid());
        if (memoriaAsignada != null) {
            memoriaUsada = Math.max(0, memoriaUsada - memoriaAsignada);
        }

        List<String> recursosAsignados = recursosPorProceso.remove(proceso.getPid());
        if (recursosAsignados != null) {
            recursosOcupados.removeAll(recursosAsignados);
        }

        liberarCPU(proceso);
        proceso.liberarRecursos();
        ultimoMensaje = "Recursos liberados por el proceso PID " + proceso.getPid() + ".";
    }

    public String getUltimoMensaje() {
        return ultimoMensaje;
    }

    public int getCpuTotal() {
        return cpuTotal;
    }

    public int getMemoriaTotal() {
        return memoriaTotal;
    }

    public int getCpuUsada() {
        return cpuUsada;
    }

    public int getMemoriaUsada() {
        return memoriaUsada;
    }

    public long getPidEnCPU() {
        return pidEnCPU;
    }

    public List<String> getRecursosOcupados() {
        return new ArrayList<>(recursosOcupados);
    }

    public void mostrarEstado() {
        System.out.println("\n===== RECURSOS DEL SISTEMA =====");
        System.out.println("CPU usada: " + cpuUsada + " / " + cpuTotal);
        System.out.println("PID en CPU: " + (pidEnCPU == -1 ? "Ninguno" : pidEnCPU));
        System.out.println("Memoria usada: " + memoriaUsada + " / " + memoriaTotal);
        System.out.println("Recursos ocupados: " + (recursosOcupados.isEmpty() ? "Ninguno" : recursosOcupados));
    }
}
