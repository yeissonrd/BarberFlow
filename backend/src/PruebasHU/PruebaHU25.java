package PruebasHU;

import modelo.*;
import java.util.ArrayList;
import java.util.List;

public class PruebaHU25 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-25: Asignar permisos a los barberos ===");

        Administrador admin = new Administrador("Sandra Perez", "sandra.hu25@ejemplo.com", "3205551234", "clave9999");
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin.hu25@ejemplo.com", "3109876543", "clave5678", "Fade");
        Barbero julian = new Barbero("Julian Gomez", "julian.hu25@ejemplo.com", "3101112222", "clave1111", "Barba");

        System.out.println();
        System.out.println("-- Criterio 1: asignar permisos --");
        System.out.println("Permisos de Kevin al inicio: " + kevin.getPermisos());

        List<String> lista = new ArrayList<>();
        lista.add("registrar_servicio");
        lista.add("ver_agenda");
        admin.asignarPermisos(kevin, lista);
        System.out.println("Kevin tiene registrar_servicio: " + kevin.tienePermiso("registrar_servicio"));
        System.out.println("Kevin tiene REGISTRAR_SERVICIO (mayusculas): " + kevin.tienePermiso("REGISTRAR_SERVICIO"));
        System.out.println("Kevin tiene gestionar_clientes: " + kevin.tienePermiso("gestionar_clientes"));

        // Modificar: se vuelve a asignar con la lista actualizada
        List<String> nueva = new ArrayList<>();
        nueva.add("ver_agenda");
        nueva.add("gestionar_clientes");
        admin.asignarPermisos(kevin, nueva);
        System.out.println("Kevin ya no tiene registrar_servicio: " + !kevin.tienePermiso("registrar_servicio"));
        System.out.println("Kevin tiene gestionar_clientes: " + kevin.tienePermiso("gestionar_clientes"));

        System.out.println();
        System.out.println("-- Permisos vinculados al perfil de cada barbero --");
        admin.asignarPermisos(julian, nueva);
        nueva.add("ver_finanzas"); // se cambia la lista original despues de asignar
        System.out.println("Lista original: " + nueva);
        System.out.println("Permisos de Julian no cambian: " + julian.getPermisos());
        System.out.println("Permisos de Kevin no cambian: " + kevin.getPermisos());
        System.out.println("Listas independientes: " + (julian.getPermisos() != kevin.getPermisos()));

        List<String> sucia = new ArrayList<>();
        sucia.add("  ver_agenda ");
        sucia.add("ver_agenda");
        sucia.add("registrar_servicio");
        admin.asignarPermisos(julian, sucia);
        System.out.println("Sin repetidos ni espacios: " + julian.getPermisos());

        // Quitar todos los permisos tambien es modificarlos
        admin.asignarPermisos(julian, new ArrayList<>());
        System.out.println("Julian sin permisos: " + julian.getPermisos().isEmpty());

        System.out.println();
        System.out.println("-- Casos rechazados --");
        try {
            admin.asignarPermisos(null, nueva);
            System.out.println("ERROR: se acepto un barbero nulo");
        } catch (IllegalArgumentException e) {
            System.out.println("OK barbero nulo: " + e.getMessage());
        }

        try {
            admin.asignarPermisos(kevin, null);
            System.out.println("ERROR: se acepto una lista nula");
        } catch (IllegalArgumentException e) {
            System.out.println("OK lista nula: " + e.getMessage());
        }

        List<String> conVacio = new ArrayList<>();
        conVacio.add("ver_agenda");
        conVacio.add("  ");
        List<String> antes = new ArrayList<>(kevin.getPermisos());
        try {
            admin.asignarPermisos(kevin, conVacio);
            System.out.println("ERROR: se acepto un permiso vacio");
        } catch (IllegalArgumentException e) {
            System.out.println("OK permiso vacio: " + e.getMessage());
        }
        System.out.println("Kevin queda como estaba: " + kevin.getPermisos().equals(antes));

        System.out.println("OK permiso nulo no se tiene: " + !kevin.tienePermiso(null));
    }
}
