package com.gestorprocesos.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.gestorprocesos.models.EstadoProceso;
import com.gestorprocesos.models.MensajeProceso;
import com.gestorprocesos.models.MotivoTerminacion;
import com.gestorprocesos.models.Proceso;
import com.gestorprocesos.synchronization.MessageQueue;
import com.gestorprocesos.utils.Logger;

public class ProcessManager {
    private final Map<Long, Proceso> procesosActivos;
    private final List<Proceso> historial;
    private final AtomicLong generadorPid;
    private final ResourceManager resourceManager;
    private final Logger logger;
    private final MessageQueue messageQueue;

    public ProcessManager(ResourceManager resourceManager, Logger logger) {
        this.procesosActivos = new LinkedHashMap<>();
        this.historial = new ArrayList<>();
        this.generadorPid = new AtomicLong(1);
        this.resourceManager = resourceManager;
        this.logger = logger;
        this.messageQueue = new MessageQueue();
    }

    public Proceso crearProceso(String nombre, int prioridad, int tiempoEjecucion, int memoriaSolicitada, List<String> recursosSolicitados) {
        Proceso proceso = new Proceso(generadorPid.getAndIncrement(), nombre, prioridad, tiempoEjecucion, memoriaSolicitada, recursosSolicitados);
        procesosActivos.put(proceso.getPid(), proceso);

        if (resourceManager.solicitarRecursos(proceso)) {
            proceso.cambiarEstado(EstadoProceso.LISTO);
            logger.registrar("PROCESO", "Creado y listo: " + proceso);
        } else {
            proceso.cambiarEstado(EstadoProceso.ESPERANDO);
            logger.registrar("RECURSOS", resourceManager.getUltimoMensaje());
            logger.registrar("PROCESO", "Creado en espera: " + proceso);
        }

        return proceso;
    }

    public void reintentarProcesosEsperando() {
        for (Proceso proceso : procesosActivos.values()) {
            if (proceso.getEstado() == EstadoProceso.ESPERANDO) {
                if (resourceManager.solicitarRecursos(proceso)) {
                    proceso.cambiarEstado(EstadoProceso.LISTO);
                    logger.registrar("RECURSOS", "Proceso PID " + proceso.getPid() + " pasó a LISTO.");
                }
            }
        }
    }

    public void suspenderProceso(long pid) {
        Proceso proceso = procesosActivos.get(pid);
        if (proceso == null) {
            logger.registrar("ERROR", "No existe el proceso PID " + pid + ".");
            return;
        }
        if (proceso.getEstado() == EstadoProceso.TERMINADO) {
            logger.registrar("ERROR", "El proceso PID " + pid + " ya fue terminado.");
            return;
        }
        proceso.cambiarEstado(EstadoProceso.SUSPENDIDO);
        logger.registrar("PROCESO", "Proceso PID " + pid + " suspendido.");
    }

    public void reanudarProceso(long pid) {
        Proceso proceso = procesosActivos.get(pid);
        if (proceso == null) {
            logger.registrar("ERROR", "No existe el proceso PID " + pid + ".");
            return;
        }
        if (proceso.getEstado() != EstadoProceso.SUSPENDIDO) {
            logger.registrar("ERROR", "El proceso PID " + pid + " no está suspendido.");
            return;
        }
        proceso.cambiarEstado(EstadoProceso.LISTO);
        logger.registrar("PROCESO", "Proceso PID " + pid + " reanudado.");
    }

    public void terminarProceso(long pid, MotivoTerminacion motivoTerminacion) {
        Proceso proceso = procesosActivos.get(pid);
        if (proceso == null) {
            logger.registrar("ERROR", "No existe el proceso PID " + pid + ".");
            return;
        }
        terminarProcesoInterno(proceso, motivoTerminacion);
    }

    public void terminarProcesoInterno(Proceso proceso, MotivoTerminacion motivoTerminacion) {
        if (proceso.getEstado() == EstadoProceso.TERMINADO) {
            return;
        }
        proceso.establecerMotivoTerminacion(motivoTerminacion);
        proceso.cambiarEstado(EstadoProceso.TERMINADO);
        resourceManager.liberarRecursos(proceso);
        procesosActivos.remove(proceso.getPid());
        historial.add(proceso);
        logger.registrar("TERMINACION", "Proceso PID " + proceso.getPid() + " finalizado por " + motivoTerminacion + ".");
    }

    public Proceso obtenerProceso(long pid) {
        return procesosActivos.get(pid);
    }

    public Collection<Proceso> obtenerProcesosActivos() {
        return procesosActivos.values();
    }

    public List<Proceso> obtenerProcesosListos() {
        List<Proceso> lista = new ArrayList<>();
        for (Proceso proceso : procesosActivos.values()) {
            if (proceso.getEstado() == EstadoProceso.LISTO) {
                lista.add(proceso);
            }
        }
        return lista;
    }

    public List<Proceso> obtenerProcesosHistorial() {
        return historial;
    }

    public void enviarMensaje(long pidOrigen, long pidDestino, String contenido) {
        Proceso origen = procesosActivos.get(pidOrigen);
        Proceso destino = procesosActivos.get(pidDestino);
        if (origen == null || destino == null) {
            logger.registrar("ERROR", "No se pudo enviar el mensaje. Verifica los PIDs.");
            return;
        }

        MensajeProceso mensaje = new MensajeProceso(pidOrigen, pidDestino, contenido);
        messageQueue.encolar(mensaje);
        procesarMensajesPendientes();
        logger.registrar("MENSAJE", "Mensaje enviado de PID " + pidOrigen + " a PID " + pidDestino + ".");
    }

    public void procesarMensajesPendientes() {
        if (messageQueue.estaVacia()) {
            return;
        }
        for (MensajeProceso mensaje : messageQueue.extraerTodos()) {
            Proceso destino = procesosActivos.get(mensaje.getDestinoPid());
            if (destino != null) {
                destino.recibirMensaje(mensaje);
            }
        }
    }

    public void mostrarProcesosActivos() {
        System.out.println("\n===== PROCESOS ACTIVOS =====");
        if (procesosActivos.isEmpty()) {
            System.out.println("No hay procesos activos.");
            return;
        }
        for (Proceso proceso : procesosActivos.values()) {
            System.out.println(proceso);
            if (!proceso.getMensajesRecibidos().isEmpty()) {
                System.out.println("  Mensajes: " + proceso.getMensajesRecibidos());
            }
        }
    }

    public void mostrarHistorial() {
        System.out.println("\n===== HISTORIAL DE PROCESOS =====");
        if (historial.isEmpty()) {
            System.out.println("Aún no hay procesos terminados.");
            return;
        }
        for (Proceso proceso : historial) {
            System.out.println(proceso);
        }
    }
}
