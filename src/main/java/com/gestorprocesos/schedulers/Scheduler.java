package com.gestorprocesos.schedulers;

import java.util.List;

import com.gestorprocesos.managers.ProcessManager;
import com.gestorprocesos.managers.ResourceManager;
import com.gestorprocesos.models.Proceso;
import com.gestorprocesos.utils.Logger;
import com.gestorprocesos.utils.StatisticsManager;

public interface Scheduler {
    String getNombre();

    void agregarProceso(Proceso proceso);

    Proceso obtenerSiguienteProceso();

    void ejecutar(ProcessManager processManager, ResourceManager resourceManager, StatisticsManager statisticsManager, Logger logger);

    List<Proceso> obtenerCola();

    void limpiarCola();
}
