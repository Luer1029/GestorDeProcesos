package com.gestorprocesos.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomDataGenerator {
    private static final Random random = new Random();
    
    private static final String[] NOMBRES_BASE = {
        "Navegador", "Compilador", "Editor", "Copia", "Descarga",
        "Música", "Video", "Chat", "Email", "Calculadora",
        "Análisis", "Render", "Sincronización", "Backup", "Scan",
        "Servidor", "Cliente", "Gateway", "Proxy", "Cache"
    };
    
    private static final String[] RECURSOS_DISPONIBLES = {
        "IMPRESORA", "DISCO-1", "DISCO-2", "RED", "GPU",
        "SCANNER", "MODEM", "PUERTO-SERIE", "USB", "AUDIO"
    };

    public static String generarNombreProceso() {
        return NOMBRES_BASE[random.nextInt(NOMBRES_BASE.length)] + "-" + random.nextInt(100);
    }

    public static int generarPrioridad() {
        return random.nextInt(1, 6);
    }

    public static int generarTiempoEjecucion() {
        return random.nextInt(2, 21);
    }

    public static int generarMemoria() {
        return 256 * (random.nextInt(1, 9));
    }

    public static List<String> generarRecursos() {
        List<String> recursos = new ArrayList<>();
        int cantidadRecursos = random.nextInt(0, 3);
        
        for (int i = 0; i < cantidadRecursos; i++) {
            String recurso = RECURSOS_DISPONIBLES[random.nextInt(RECURSOS_DISPONIBLES.length)];
            if (!recursos.contains(recurso)) {
                recursos.add(recurso);
            }
        }
        
        return recursos;
    }

    public static int generarCantidadProcesos() {
        return random.nextInt(3, 9);
    }
}
