package com.gestorprocesos.schedulers;

import com.gestorprocesos.models.Proceso;

public class FcfsScheduler extends AbstractScheduler {
    public FcfsScheduler() {
        super("FCFS");
    }

    @Override
    protected int calcularTiempoEjecucion(Proceso proceso) {
        return proceso.getTiempoRestante();
    }
}
