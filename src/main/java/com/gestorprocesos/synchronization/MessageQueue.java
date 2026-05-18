package com.gestorprocesos.synchronization;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.gestorprocesos.models.MensajeProceso;

public class MessageQueue {
    private final Deque<MensajeProceso> colaMensajes = new ArrayDeque<>();

    public synchronized void encolar(MensajeProceso mensaje) {
        colaMensajes.addLast(mensaje);
    }

    public synchronized List<MensajeProceso> extraerTodos() {
        List<MensajeProceso> mensajes = new ArrayList<>(colaMensajes);
        colaMensajes.clear();
        return mensajes;
    }

    public synchronized boolean estaVacia() {
        return colaMensajes.isEmpty();
    }
}
