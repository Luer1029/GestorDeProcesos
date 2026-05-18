package com.gestorprocesos.schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.gestorprocesos.managers.ProcessManager;
import com.gestorprocesos.managers.ResourceManager;
import com.gestorprocesos.models.EstadoProceso;
import com.gestorprocesos.models.MotivoTerminacion;
import com.gestorprocesos.models.Proceso;
import com.gestorprocesos.utils.Logger;
import com.gestorprocesos.utils.StatisticsManager;
import com.gestorprocesos.utils.TimeSimulator;

public abstract class AbstractScheduler implements Scheduler {
    protected final List<Proceso> colaProcesos;
    private final String nombre;

    protected AbstractScheduler(String nombre) {
        this.nombre = nombre;
        this.colaProcesos = new ArrayList<>();
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void agregarProceso(Proceso proceso) {
        if (proceso != null && proceso.getEstado() == EstadoProceso.LISTO) {
            colaProcesos.add(proceso);
        }
    }

    @Override
    public Proceso obtenerSiguienteProceso() {
        if (colaProcesos.isEmpty()) {
            return null;
        }
        Proceso seleccionado = seleccionarSiguienteProceso();
        colaProcesos.remove(seleccionado);
        return seleccionado;
    }

    @Override
    public void ejecutar(ProcessManager processManager, ResourceManager resourceManager, StatisticsManager statisticsManager, Logger logger) {
        logger.registrar("SCHEDULER", "Iniciando planificador " + nombre + ".");
        while (!colaProcesos.isEmpty()) {
            Proceso proceso = obtenerSiguienteProceso();
            if (proceso == null) {
                break;
            }

            if (proceso.getEstado() != EstadoProceso.LISTO) {
                continue;
            }

            if (!resourceManager.solicitarCPU(proceso)) {
                logger.registrar("CPU", resourceManager.getUltimoMensaje());
                colaProcesos.add(proceso);
                TimeSimulator.simularPaso(60);
                continue;
            }

            proceso.cambiarEstado(EstadoProceso.EJECUTANDO);
            logger.registrar("EJECUCIÓN", "Proceso PID " + proceso.getPid() + " ejecutándose con " + nombre + ".");

            int tiempoConsumido = calcularTiempoEjecucion(proceso);
            TimeSimulator.simularPaso(120);
            proceso.reducirTiempoRestante(tiempoConsumido);
            resourceManager.liberarCPU(proceso);

            if (proceso.estaFinalizado()) {
                processManager.terminarProcesoInterno(proceso, MotivoTerminacion.FINALIZACION_CORRECTA);
                statisticsManager.registrarProcesoTerminado(proceso);
            } else {
                proceso.cambiarEstado(EstadoProceso.LISTO);
                colaProcesos.add(proceso);
                logger.registrar("PLANIFICACIÓN", "Proceso PID " + proceso.getPid() + " regresa a la cola con " + proceso.getTiempoRestante() + " unidades restantes.");
            }
        }
        logger.registrar("SCHEDULER", "Finalizó el planificador " + nombre + ".");
    }

    @Override
    public List<Proceso> obtenerCola() {
        return Collections.unmodifiableList(colaProcesos);
    }

    @Override
    public void limpiarCola() {
        colaProcesos.clear();
    }

    protected abstract int calcularTiempoEjecucion(Proceso proceso);

    protected Proceso seleccionarSiguienteProceso() {
        Comparator<Proceso> comparator = obtenerComparador();
        if (comparator == null) {
            return colaProcesos.get(0);
        }
        return colaProcesos.stream().min(comparator).orElse(colaProcesos.get(0));
    }

    protected Comparator<Proceso> obtenerComparador() {
        return null;
    }
}
