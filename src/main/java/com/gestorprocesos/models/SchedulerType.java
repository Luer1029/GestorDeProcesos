package com.gestorprocesos.models;

public enum SchedulerType {
    FCFS("FCFS"),
    SJF("SJF"),
    ROUND_ROBIN("Round Robin"),
    PRIORIDADES("Prioridades");

    private final String etiqueta;

    SchedulerType(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
