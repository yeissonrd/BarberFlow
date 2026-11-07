package PruebasHU;

import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;

public class PruebaHU29 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-29: Configurar el horario de atencion ===");

        Administrador sandra = new Administrador("Sandra Perez", "sandra.hu29@ejemplo.com", "3205551234", "clave9999");
        Cliente andres = new Cliente("Andres Torres", "andres.hu29@ejemplo.com", "3001234567", "clave1234", 27);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin.hu29@ejemplo.com", "3109876543", "clave5678", "Fade");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY);
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre dé lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        System.out.println("Horario por defecto: " + Barberia.getHorarioAtencion());

        // Criterio 1: la administradora define un nuevo horario
        // (desordenado y con una hora repetida, para mostrar que se ordena y se limpia)
        System.out.println("\n-- Criterio 1: definir el horario --");
        List<LocalTime> nuevo = Arrays.asList(
            LocalTime.of(18, 0), LocalTime.of(8, 0), LocalTime.of(9, 0), LocalTime.of(10, 0),
            LocalTime.of(11, 0), LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(9, 0));
        sandra.configurarHorarioAtencion(nuevo);
        System.out.println("Nuevo horario: " + Barberia.getHorarioAtencion());

        // Criterio 2: a los clientes solo se les ofrecen horas del horario vigente
        System.out.println("\n-- Criterio 2: solo horas del horario vigente --");
        System.out.println("Libres de Kevin el lunes: " + kevin.horariosDisponibles(lunes));
        Cita temprano = andres.agendarCita(kevin, corte, lunes, LocalTime.of(8, 0));
        System.out.println("OK nueva hora disponible: " + temprano.resumen());
        try {
            andres.agendarCita(kevin, corte, lunes, LocalTime.of(12, 0));
            System.out.println("ERROR: se agendo una hora que ya no esta en el horario");
        } catch (IllegalArgumentException e) {
            System.out.println("OK hora retirada del horario: " + e.getMessage());
        }

        // Criterio 1: la administradora modifica el horario (solo en la mañana)
        System.out.println("\n-- Criterio 1: modificar el horario --");
        sandra.configurarHorarioAtencion(Arrays.asList(
            LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(12, 0)));
        System.out.println("Horario modificado: " + Barberia.getHorarioAtencion());
        System.out.println("Libres de Kevin el lunes: " + kevin.horariosDisponibles(lunes));
        try {
            andres.agendarCita(kevin, corte, lunes, LocalTime.of(15, 0));
            System.out.println("ERROR: se agendo en la tarde con horario solo de manana");
        } catch (IllegalArgumentException e) {
            System.out.println("OK fuera del horario vigente: " + e.getMessage());
        }
        System.out.println("La cita ya agendada se conserva: " + temprano.resumen());

        // Casos que el sistema debe rechazar (el horario no cambia)
        System.out.println("\n-- Casos rechazados --");
        try {
            sandra.configurarHorarioAtencion(null);
            System.out.println("ERROR: se acepto una lista nula");
        } catch (IllegalArgumentException e) {
            System.out.println("OK lista nula: " + e.getMessage());
        }
        try {
            sandra.configurarHorarioAtencion(Arrays.asList());
            System.out.println("ERROR: se acepto una lista vacia");
        } catch (IllegalArgumentException e) {
            System.out.println("OK lista vacia: " + e.getMessage());
        }
        try {
            sandra.configurarHorarioAtencion(Arrays.asList(LocalTime.of(9, 0), null));
            System.out.println("ERROR: se acepto una hora vacia");
        } catch (IllegalArgumentException e) {
            System.out.println("OK hora vacia: " + e.getMessage());
        }
        System.out.println("El horario sigue igual: " + Barberia.getHorarioAtencion());
    }
}