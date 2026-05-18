package com.gestorprocesos.synchronization;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.gestorprocesos.utils.Logger;

public class ProducerConsumerSimulation {
    public void ejecutar(Logger logger) {
        final int capacidad = 3;
        final Deque<String> buffer = new ArrayDeque<>();
        final Semaphore espaciosDisponibles = new Semaphore(capacidad);
        final Semaphore elementosDisponibles = new Semaphore(0);
        final Semaphore mutex = new Semaphore(1);

        Thread productor = new Thread(() -> {
            for (int i = 1; i <= 6; i++) {
                boolean espacioAdquirido = false;
                boolean mutexAdquirido = false;
                try {
                    if (espaciosDisponibles.availablePermits() == 0) {
                        logger.registrar("SINCRONIZACIÓN", "Productor en espera: buffer lleno.");
                    }
                    espaciosDisponibles.acquire();
                    espacioAdquirido = true;
                    mutex.acquire();
                    mutexAdquirido = true;
                    String elemento = "Dato-" + i;
                    buffer.addLast(elemento);
                    logger.registrar("PRODUCTOR", "Produjo " + elemento + ". Buffer: " + buffer);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    if (mutexAdquirido) {
                        mutex.release();
                    }
                    if (espacioAdquirido) {
                        elementosDisponibles.release();
                    }
                }

                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(80));
            }
        });

        Thread consumidor = new Thread(() -> {
            for (int i = 1; i <= 6; i++) {
                boolean elementoAdquirido = false;
                boolean mutexAdquirido = false;
                try {
                    if (elementosDisponibles.availablePermits() == 0) {
                        logger.registrar("SINCRONIZACIÓN", "Consumidor en espera: buffer vacío.");
                    }
                    elementosDisponibles.acquire();
                    elementoAdquirido = true;
                    mutex.acquire();
                    mutexAdquirido = true;
                    String elemento = buffer.removeFirst();
                    logger.registrar("CONSUMIDOR", "Consumió " + elemento + ". Buffer: " + buffer);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    if (mutexAdquirido) {
                        mutex.release();
                    }
                    if (elementoAdquirido) {
                        espaciosDisponibles.release();
                    }
                }

                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(140));
            }
        });

        logger.registrar("SINCRONIZACIÓN", "Iniciando simulación productor-consumidor.");
        productor.start();
        consumidor.start();

        try {
            productor.join();
            consumidor.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        logger.registrar("SINCRONIZACIÓN", "Finalizó la simulación productor-consumidor.");
    }
}
