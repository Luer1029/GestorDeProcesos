package com.gestorprocesos.schedulers;

import java.util.Comparator;

import com.gestorprocesos.models.Proceso;

public class SjfScheduler extends AbstractScheduler {
    public SjfScheduler() {
        super("SJF");
    }

    @Override
    protected int calcularTiempoEjecucion(Proceso proceso) {
        return proceso.getTiempoRestante();
    }

    @Override
    protected Comparator<Proceso> obtenerComparador() {
        return Comparator.comparingInt(Proceso::getTiempoRestante).thenComparingLong(Proceso::getPid);
    }
}
