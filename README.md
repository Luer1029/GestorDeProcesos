# Simulador de Gestor de Procesos para Sistemas Operativos

Proyecto en Java orientado a objetos que simula un gestor de procesos de sistema operativo desde consola. Incluye administración de procesos, recursos, algoritmos de planificación, sincronización y registro de eventos.

## Características

- Creación, suspensión, reanudación y terminación de procesos.
- Gestión de CPU, memoria y recursos compartidos.
- Planificadores FCFS, SJF, Round Robin y Prioridades.
- Simulación de semáforos y productor-consumidor.
- Comunicación mediante mensajes entre procesos.
- Logs ordenados en consola.
- Estadísticas de ejecución y uso de memoria.
- Menú interactivo por consola.
- Generación automática de datos de prueba.

## Arquitectura

El proyecto está organizado por paquetes para mantener responsabilidad única y facilidad de mantenimiento:

- `com.gestorprocesos.main`: punto de entrada y menú CLI.
- `com.gestorprocesos.models`: entidades y enums del dominio.
- `com.gestorprocesos.managers`: gestión de procesos y recursos.
- `com.gestorprocesos.schedulers`: estrategia de planificación.
- `com.gestorprocesos.synchronization`: comunicación y sincronización.
- `com.gestorprocesos.utils`: utilidades, logs y estadísticas.

### Flujo general

1. El usuario crea o carga procesos.
2. `ProcessManager` valida y registra cada proceso.
3. `ResourceManager` asigna memoria y recursos disponibles.
4. El `Scheduler` seleccionado ejecuta los procesos listos.
5. Los procesos terminados pasan al historial y liberan recursos.
6. `Logger` y `StatisticsManager` registran toda la actividad.

## Algoritmos de planificación

### FCFS
Ejecuta en el orden en que los procesos llegan. Es el algoritmo más simple y no expulsa procesos.

### SJF
Selecciona primero el proceso con menor tiempo restante de ejecución. Favorece trabajos cortos.

### Round Robin
Ejecuta cada proceso por un quantum configurables y luego lo devuelve a la cola si no terminó. Es útil para repartir CPU de forma equitativa.

### Prioridades
Selecciona primero el proceso con mayor prioridad. En este simulador, un número menor representa una prioridad más alta.

## Sincronización y comunicación

- La simulación productor-consumidor utiliza semáforos de Java para representar exclusión mutua y control de buffer.
- Los procesos pueden enviar mensajes a otros procesos mediante una cola de mensajes interna.

## Requisitos

- Java 17 o superior.
- Maven 3.8+.

## Ejecutar

Desde la raíz del proyecto:

```bash
mvn clean package
mvn exec:java
```

También puedes ejecutar directamente la clase principal:

```bash
java -cp target/classes com.gestorprocesos.main.App
```

## Menú principal

El programa ofrece opciones para:

- Generar datos de prueba.
- Crear procesos.
- Mostrar procesos activos e ისტორico.
- Ejecutar el planificador.
- Suspender, reanudar y terminar procesos.
- Ver recursos, logs, cola de planificación y estadísticas.
- Cambiar el algoritmo de planificación.
- Enviar mensajes entre procesos.
- Simular productor-consumidor.

## Ejemplo de uso

1. Inicia la aplicación.
2. Usa la opción de datos de prueba o crea procesos manualmente.
3. Selecciona un algoritmo de planificación.
4. Ejecuta el scheduler.
5. Revisa los logs, la cola de planificación y las estadísticas.

## Notas

- La simulación prioriza claridad y trazabilidad sobre realismo a nivel de kernel.
- Los tiempos se representan como unidades lógicas con pausas cortas para permitir observar la ejecución.
