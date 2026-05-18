package com.gestorprocesos.main;

import java.util.List;
import java.util.Scanner;

import com.gestorprocesos.managers.ProcessManager;
import com.gestorprocesos.managers.ResourceManager;
import com.gestorprocesos.models.MotivoTerminacion;
import com.gestorprocesos.models.Proceso;
import com.gestorprocesos.models.SchedulerType;
import com.gestorprocesos.schedulers.Scheduler;
import com.gestorprocesos.schedulers.SchedulerFactory;
import com.gestorprocesos.synchronization.ProducerConsumerSimulation;
import com.gestorprocesos.utils.ConsoleInput;
import com.gestorprocesos.utils.Logger;
import com.gestorprocesos.utils.RandomDataGenerator;
import com.gestorprocesos.utils.StatisticsManager;

public class App {
    private final Logger logger;
    private final ResourceManager resourceManager;
    private final ProcessManager processManager;
    private final StatisticsManager statisticsManager;
    private final ProducerConsumerSimulation producerConsumerSimulation;

    private SchedulerType schedulerType;
    private Scheduler scheduler;
    private int quantum;

    public App() {
        this.logger = new Logger();
        this.resourceManager = new ResourceManager(1, 4096);
        this.processManager = new ProcessManager(resourceManager, logger);
        this.statisticsManager = new StatisticsManager();
        this.producerConsumerSimulation = new ProducerConsumerSimulation();
        this.schedulerType = SchedulerType.FCFS;
        this.quantum = 2;
        this.scheduler = SchedulerFactory.crear(schedulerType, quantum);
    }

    public static void main(String[] args) {
        new App().ejecutar();
    }

    private void ejecutar() {
        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleInput input = new ConsoleInput(scanner);
            generarDatosPrueba();

            boolean salir = false;
            while (!salir) {
                mostrarMenu();
                int opcion = input.leerEntero("Selecciona una opción: ", 1, 15);

                switch (opcion) {
                    case 1 -> generarDatosPrueba();
                    case 2 -> crearProcesoInteractivo(input);
                    case 3 -> mostrarProcesos();
                    case 4 -> ejecutarScheduler();
                    case 5 -> suspenderProceso(input);
                    case 6 -> reanudarProceso(input);
                    case 7 -> terminarProceso(input);
                    case 8 -> resourceManager.mostrarEstado();
                    case 9 -> logger.mostrarLogs();
                    case 10 -> cambiarAlgoritmo(input);
                    case 11 -> enviarMensaje(input);
                    case 12 -> simularSincronizacion();
                    case 13 -> mostrarColaPlanificacion();
                    case 14 -> statisticsManager.mostrarEstadisticas(resourceManager);
                    case 15 -> salir = true;
                    default -> System.out.println("Opción no válida.");
                }
            }
        }

        System.out.println("Saliendo del simulador...");
    }

    private void mostrarMenu() {
        System.out.println("\n==== SIMULADOR DE GESTOR DE PROCESOS ====");
        System.out.println("Algoritmo actual: " + schedulerType.getEtiqueta() + (schedulerType == SchedulerType.ROUND_ROBIN ? " (quantum=" + quantum + ")" : ""));
        System.out.println("1. Generar datos de prueba");
        System.out.println("2. Crear proceso");
        System.out.println("3. Mostrar procesos");
        System.out.println("4. Ejecutar scheduler");
        System.out.println("5. Suspender proceso");
        System.out.println("6. Reanudar proceso");
        System.out.println("7. Terminar proceso");
        System.out.println("8. Mostrar recursos");
        System.out.println("9. Mostrar logs");
        System.out.println("10. Cambiar algoritmo");
        System.out.println("11. Enviar mensaje entre procesos");
        System.out.println("12. Simular productor-consumidor");
        System.out.println("13. Mostrar cola de planificación");
        System.out.println("14. Mostrar estadísticas");
        System.out.println("15. Salir");
    }

    private void generarDatosPrueba() {
        int cantidadProcesos = RandomDataGenerator.generarCantidadProcesos();
        for (int i = 0; i < cantidadProcesos; i++) {
            String nombre = RandomDataGenerator.generarNombreProceso();
            int prioridad = RandomDataGenerator.generarPrioridad();
            int tiempoEjecucion = RandomDataGenerator.generarTiempoEjecucion();
            int memoria = RandomDataGenerator.generarMemoria();
            List<String> recursos = RandomDataGenerator.generarRecursos();
            
            processManager.crearProceso(nombre, prioridad, tiempoEjecucion, memoria, recursos);
        }
        recargarColaPlanificacion();
        logger.registrar("DATOS", "Se generaron " + cantidadProcesos + " procesos aleatorios.");
    }

    private void crearProcesoInteractivo(ConsoleInput input) {
        String nombre = input.leerTextoNoVacio("Nombre del proceso: ");
        int prioridad = input.leerEntero("Prioridad (1 = más alta, 5 = más baja): ", 1, 5);
        int tiempoEjecucion = input.leerEntero("Tiempo de ejecución: ", 1, 1000);
        int memoria = input.leerEntero("Memoria solicitada: ", 1, 4096);
        List<String> recursos = input.leerListaRecursos("Recursos solicitados separados por coma (enter para ninguno): ");

        processManager.crearProceso(nombre, prioridad, tiempoEjecucion, memoria, recursos);
        recargarColaPlanificacion();
    }

    private void mostrarProcesos() {
        processManager.mostrarProcesosActivos();
        processManager.mostrarHistorial();
    }

    private void ejecutarScheduler() {
        recargarColaPlanificacion();
        if (scheduler.obtenerCola().isEmpty()) {
            System.out.println("No hay procesos listos para ejecutar.");
            return;
        }

        scheduler.ejecutar(processManager, resourceManager, statisticsManager, logger);
        recargarColaPlanificacion();
    }

    private void suspenderProceso(ConsoleInput input) {
        long pid = input.leerLong("PID a suspender: ", 1);
        processManager.suspenderProceso(pid);
        recargarColaPlanificacion();
    }

    private void reanudarProceso(ConsoleInput input) {
        long pid = input.leerLong("PID a reanudar: ", 1);
        processManager.reanudarProceso(pid);
        recargarColaPlanificacion();
    }

    private void terminarProceso(ConsoleInput input) {
        long pid = input.leerLong("PID a terminar: ", 1);
        System.out.println("Motivo de terminación:");
        System.out.println("1. Finalización correcta");
        System.out.println("2. Error");
        System.out.println("3. Interbloqueo");
        System.out.println("4. Terminación manual");
        int opcion = input.leerEntero("Selecciona el motivo: ", 1, 4);

        MotivoTerminacion motivo = switch (opcion) {
            case 1 -> MotivoTerminacion.FINALIZACION_CORRECTA;
            case 2 -> MotivoTerminacion.ERROR;
            case 3 -> MotivoTerminacion.INTERBLOQUEO;
            default -> MotivoTerminacion.TERMINACION_MANUAL;
        };

        processManager.terminarProceso(pid, motivo);
        recargarColaPlanificacion();
    }

    private void cambiarAlgoritmo(ConsoleInput input) {
        System.out.println("Algoritmos disponibles:");
        System.out.println("1. FCFS");
        System.out.println("2. SJF");
        System.out.println("3. Round Robin");
        System.out.println("4. Prioridades");

        int opcion = input.leerEntero("Selecciona el algoritmo: ", 1, 4);
        schedulerType = switch (opcion) {
            case 1 -> SchedulerType.FCFS;
            case 2 -> SchedulerType.SJF;
            case 3 -> SchedulerType.ROUND_ROBIN;
            default -> SchedulerType.PRIORIDADES;
        };

        if (schedulerType == SchedulerType.ROUND_ROBIN) {
            quantum = input.leerEntero("Quantum para Round Robin: ", 1, 20);
        }

        scheduler = SchedulerFactory.crear(schedulerType, quantum);
        recargarColaPlanificacion();
        System.out.println("Algoritmo actualizado a " + schedulerType.getEtiqueta() + ".");
    }

    private void enviarMensaje(ConsoleInput input) {
        long origen = input.leerLong("PID origen: ", 1);
        long destino = input.leerLong("PID destino: ", 1);
        String contenido = input.leerTextoNoVacio("Mensaje: ");
        processManager.enviarMensaje(origen, destino, contenido);
    }

    private void simularSincronizacion() {
        producerConsumerSimulation.ejecutar(logger);
    }

    private void mostrarColaPlanificacion() {
        recargarColaPlanificacion();
        System.out.println("\n===== COLA DE PLANIFICACIÓN - " + scheduler.getNombre() + " =====");
        if (scheduler.obtenerCola().isEmpty()) {
            System.out.println("No hay procesos en la cola.");
            return;
        }
        for (Proceso proceso : scheduler.obtenerCola()) {
            System.out.println(proceso);
        }
    }

    private void recargarColaPlanificacion() {
        scheduler.limpiarCola();
        processManager.reintentarProcesosEsperando();
        for (Proceso proceso : processManager.obtenerProcesosListos()) {
            scheduler.agregarProceso(proceso);
        }
    }
}