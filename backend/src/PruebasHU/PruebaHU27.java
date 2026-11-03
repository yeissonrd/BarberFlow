package PruebasHU;

import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU27 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-27: Reprogramar cualquier cita ===");

        Administrador sandra = new Administrador("Sandra Perez", "sandra.hu27@ejemplo.com", "3205551234", "clave9999");
        Cliente andres = new Cliente("Andres Torres", "andres.hu27@ejemplo.com", "3001234567", "clave1234", 27);
        Cliente laura = new Cliente("Laura Mejia", "laura.hu27@ejemplo.com", "3105550101", "clave4321", 31);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin.hu27@ejemplo.com", "3109876543", "clave5678", "Fade");
        Barbero julian = new Barbero("Julian Gomez", "julian.hu27@ejemplo.com", "3101112222", "clave1111", "Barba");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY);
        julian.setDiaDescanso(DayOfWeek.WEDNESDAY);
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre de lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // Criterio 1: la administradora reprograma citas de cualquier barbero
        System.out.println("\n-- Criterio 1: reprogramar citas de cualquier barbero --");
        Cita citaKevin = andres.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        Cita citaJulian = laura.agendarCita(julian, corte, lunes, LocalTime.of(15, 0));
        sandra.reprogramarCita(citaKevin, lunes, LocalTime.of(16, 0));
        sandra.reprogramarCita(citaJulian, lunes, LocalTime.of(9, 0));
        System.out.println(citaKevin.resumen());
        System.out.println(citaJulian.resumen());

        // Criterio 2: el horario anterior queda libre y el nuevo queda ocupado
        System.out.println("\n-- Criterio 2: nunca dos citas del mismo barbero en el mismo horario --");
        System.out.println("Libres de Kevin el lunes: " + kevin.horariosDisponibles(lunes));
        Cita nueva = laura.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        System.out.println("Laura toma el horario liberado: " + nueva.resumen());
        try {
            sandra.reprogramarCita(nueva, lunes, LocalTime.of(16, 0));
            System.out.println("ERROR: se asignaron dos citas al mismo barbero en el mismo horario");
        } catch (IllegalArgumentException e) {
            System.out.println("OK horario ocupado: " + e.getMessage());
        }
        System.out.println("La cita de Laura sigue a las: " + nueva.getHora());

        // Criterio 1: en cualquier momento, aunque falten menos de 2 horas
        System.out.println("\n-- Criterio 1: en cualquier momento --");
        Cita urgente = andres.agendarCita(kevin, corte, lunes, LocalTime.of(11, 0));
        LocalDateTime enUnaHora = LocalDateTime.now().plusHours(1).withSecond(0).withNano(0); // simulamos que la cita es en 1 hora
        urgente.setFecha(enUnaHora.toLocalDate());
        urgente.setHora(enUnaHora.toLocalTime());
        try {
            andres.reprogramarCita(urgente, lunes, LocalTime.of(17, 0));
            System.out.println("ERROR: el cliente reprogramo con menos de 2 horas");
        } catch (RuntimeException e) {
            System.out.println("OK el cliente no puede: " + e.getMessage());
        }
        sandra.reprogramarCita(urgente, lunes, LocalTime.of(17, 0));
        System.out.println("OK la administradora si puede: " + urgente.resumen());

        // Casos que el sistema debe rechazar
        System.out.println("\n-- Casos rechazados --");
        try {
            sandra.reprogramarCita(null, lunes, LocalTime.of(9, 0));
            System.out.println("ERROR: se reprogramo una cita nula");
        } catch (IllegalArgumentException e) {
            System.out.println("OK cita nula: " + e.getMessage());
        }
        try {
            sandra.reprogramarCita(nueva, null, LocalTime.of(9, 0));
            System.out.println("ERROR: se reprogramo sin fecha");
        } catch (IllegalArgumentException e) {
            System.out.println("OK sin fecha: " + e.getMessage());
        }
        sandra.cancelarCita(citaJulian);
        try {
            sandra.reprogramarCita(citaJulian, lunes, LocalTime.of(11, 0));
            System.out.println("ERROR: se reprogramo una cita cancelada");
        } catch (IllegalStateException e) {
            System.out.println("OK cita cancelada: " + e.getMessage());
        }
        try {
            sandra.reprogramarCita(nueva, lunes.plusDays(1), LocalTime.of(9, 0));
            System.out.println("ERROR: se reprogramo al dia de descanso");
        } catch (IllegalArgumentException e) {
            System.out.println("OK dia de descanso: " + e.getMessage());
        }
        try {
            sandra.reprogramarCita(nueva, lunes, LocalTime.of(13, 0));
            System.out.println("ERROR: se reprogramo fuera del horario de atencion");
        } catch (IllegalArgumentException e) {
            System.out.println("OK fuera de horario: " + e.getMessage());
        }
    }
}