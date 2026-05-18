package com.gestorprocesos.utils;

public final class TimeSimulator {
    private TimeSimulator() {
    }

    public static void simularPaso(int milisegundos) {
        try {
            Thread.sleep(Math.max(1, Math.min(milisegundos, 250)));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
