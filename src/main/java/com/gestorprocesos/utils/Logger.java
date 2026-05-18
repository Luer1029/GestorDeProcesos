package com.gestorprocesos.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Logger {
    private final List<String> registros = new ArrayList<>();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void registrar(String categoria, String mensaje) {
        String entrada = "[" + LocalDateTime.now().format(formatter) + "] [" + categoria + "] " + mensaje;
        registros.add(entrada);
        System.out.println(entrada);
    }

    public void mostrarLogs() {
        if (registros.isEmpty()) {
            System.out.println("No hay logs registrados.");
            return;
        }

        System.out.println("\n===== LOGS DEL SISTEMA =====");
        for (int i = 0; i < registros.size(); i++) {
            System.out.println((i + 1) + ". " + registros.get(i));
        }
    }

    public List<String> obtenerRegistros() {
        return Collections.unmodifiableList(registros);
    }
}
