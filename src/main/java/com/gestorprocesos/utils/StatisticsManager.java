package com.gestorprocesos.utils;

import com.gestorprocesos.managers.ResourceManager;
import com.gestorprocesos.models.Proceso;

public class StatisticsManager {
    private int procesosEjecutados;
    private long tiempoTotalEjecucion;

    public void registrarProcesoTerminado(Proceso proceso) {
        procesosEjecutados++;
        tiempoTotalEjecucion += proceso.getTiempoEjecucionOriginal();
    }

    public void mostrarEstadisticas(ResourceManager resourceManager) {
        System.out.println("\n===== ESTADÍSTICAS DEL SISTEMA =====");
        System.out.println("Procesos ejecutados: " + procesosEjecutados);
        System.out.println("Tiempo promedio de ejecución: " + obtenerTiempoPromedio() + " unidades");
        System.out.println("Memoria usada actual: " + resourceManager.getMemoriaUsada() + " / " + resourceManager.getMemoriaTotal());
        System.out.println("CPU en uso: " + resourceManager.getCpuUsada() + " / " + resourceManager.getCpuTotal());
    }

    public double obtenerTiempoPromedio() {
        if (procesosEjecutados == 0) {
            return 0.0;
        }
        return (double) tiempoTotalEjecucion / procesosEjecutados;
    }
}
