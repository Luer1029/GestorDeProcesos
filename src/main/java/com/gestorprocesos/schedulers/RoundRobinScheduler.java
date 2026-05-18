package com.gestorprocesos.schedulers;

import com.gestorprocesos.models.Proceso;

public class RoundRobinScheduler extends AbstractScheduler {
    private int quantum;

    public RoundRobinScheduler(int quantum) {
        super("Round Robin");
        this.quantum = Math.max(1, quantum);
    }

    public void setQuantum(int quantum) {
        this.quantum = Math.max(1, quantum);
    }

    public int getQuantum() {
        return quantum;
    }

    @Override
    protected int calcularTiempoEjecucion(Proceso proceso) {
        return Math.min(quantum, proceso.getTiempoRestante());
    }
}
