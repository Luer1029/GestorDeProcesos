package com.gestorprocesos.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public String leerTextoNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            System.out.println("El valor no puede estar vacío.");
        }
    }

    public int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor < minimo || valor > maximo) {
                    System.out.println("El valor debe estar entre " + minimo + " y " + maximo + ".");
                    continue;
                }
                return valor;
            } catch (NumberFormatException ex) {
                System.out.println("Ingresa un número entero válido.");
            }
        }
    }

    public long leerLong(String mensaje, long minimo) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                long valor = Long.parseLong(entrada);
                if (valor < minimo) {
                    System.out.println("El valor debe ser mayor o igual a " + minimo + ".");
                    continue;
                }
                return valor;
            } catch (NumberFormatException ex) {
                System.out.println("Ingresa un número válido.");
            }
        }
    }

    public List<String> leerListaRecursos(String mensaje) {
        System.out.print(mensaje);
        String entrada = scanner.nextLine().trim();
        if (entrada.isEmpty()) {
            return List.of();
        }

        String[] partes = entrada.split(",");
        List<String> recursos = new ArrayList<>();
        for (String parte : partes) {
            String recurso = parte.trim();
            if (!recurso.isEmpty()) {
                recursos.add(recurso.toUpperCase());
            }
        }
        return recursos;
    }
}
