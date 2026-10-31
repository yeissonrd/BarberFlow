package PruebasHU;

import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU28 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-28: Cancelar cualquier cita ===");

        Administrador sandra = new Administrador("Sandra Perez", "sandra.hu28@ejemplo.com", "3205551234", "clave9999");
        Cliente andres = new Cliente("Andres Torres", "andres.hu28@ejemplo.com", "3001234567", "clave1234", 27);
        Cliente laura = new Cliente("Laura Mejia", "laura.hu28@ejemplo.com", "3105550101", "clave4321", 31);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin.hu28@ejemplo.com", "3109876543", "clave5678", "Fade");
        Barbero julian = new Barbero("Julian Gomez", "julian.hu28@ejemplo.com", "3101112222", "clave1111", "Barba");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY);
        julian.setDiaDescanso(DayOfWeek.WEDNESDAY);
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre de lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // Criterio 1: la administradora cancela citas de cualquier barbero
        System.out.println("\n-- Criterio 1: cancelar citas de cualquier barbero --");
        Cita citaKevin = andres.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        Cita citaJulian = laura.agendarCita(julian, corte, lunes, LocalTime.of(15, 0));
        sandra.cancelarCita(citaKevin);
        sandra.cancelarCita(citaJulian);
        System.out.println(citaKevin.resumen());
        System.out.println(citaJulian.resumen());

        // Criterio 2: el horario queda libre y otro cliente puede tomarlo
        System.out.println("\n-- Criterio 2: el horario se libera --");
        System.out.println("Libres de Kevin el lunes:  " + kevin.horariosDisponibles(lunes));
        System.out.println("Libres de Julian el lunes: " + julian.horariosDisponibles(lunes));
        Cita nueva = laura.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        System.out.println("Laura toma el horario liberado: " + nueva.resumen());

        // Criterio 1: en cualquier momento, aunque falten menos de 2 horas
        System.out.println("\n-- Criterio 1: en cualquier momento --");
        Cita urgente = andres.agendarCita(kevin, corte, lunes, LocalTime.of(11, 0));
        LocalDateTime enUnaHora = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0); // simulamos que la cita es en 1 hora
        urgente.setFecha(enUnaHora.toLocalDate());
        urgente.setHora(enUnaHora.toLocalTime());
        try {
            andres.cancelarCita(urgente);
            System.out.println("ERROR: el cliente cancelo con menos de 2 horas");
        } catch (IllegalStateException e) {
            System.out.println("OK el cliente no puede: " + e.getMessage());
        }
        sandra.cancelarCita(urgente);
        System.out.println("OK la administradora si puede: " + urgente.resumen());

        // Casos que el sistema debe rechazar
        System.out.println("\n-- Casos rechazados --");
        try {
            sandra.cancelarCita(null);
            System.out.println("ERROR: se cancelo una cita nula");
        } catch (IllegalArgumentException e) {
            System.out.println("OK cita nula: " + e.getMessage());
        }
        try {
            sandra.cancelarCita(citaKevin);
            System.out.println("ERROR: se cancelo dos veces la misma cita");
        } catch (IllegalStateException e) {
            System.out.println("OK ya cancelada: " + e.getMessage());
        }
    }
}