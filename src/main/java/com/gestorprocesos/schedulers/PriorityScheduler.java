package com.gestorprocesos.schedulers;

import java.util.Comparator;

import com.gestorprocesos.models.Proceso;

public class PriorityScheduler extends AbstractScheduler {
    public PriorityScheduler() {
        super("Prioridades");
    }

    @Override
    protected int calcularTiempoEjecucion(Proceso proceso) {
        return proceso.getTiempoRestante();
    }

    @Override
    protected Comparator<Proceso> obtenerComparador() {
        return Comparator.comparingInt(Proceso::getPrioridad).thenComparingLong(Proceso::getPid);
    }
}
