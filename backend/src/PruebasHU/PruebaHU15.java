package PruebasHU;

import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU15 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-15: Reprogramar una cita ===");

        Cliente andres = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        Cliente laura = new Cliente("Laura Mejia", "laura@ejemplo.com", "3105550101", "clave4321", 31);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Barba y perfilado");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY);
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre de lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate martes = lunes.plusDays(1);

        // Criterio 1: reprogramar a un nuevo horario disponible del mismo barbero
        System.out.println("\n-- Criterio 1: reprogramacion permitida --");
        Cita cita = andres.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        System.out.println("Antes:   " + cita.resumen());
        System.out.println("Libres antes:   " + kevin.horariosDisponibles(lunes));
        andres.reprogramarCita(cita, lunes, LocalTime.of(15, 0));
        System.out.println("Despues: " + cita.resumen());
        System.out.println("Libres despues: " + kevin.horariosDisponibles(lunes));

        // El horario anterior quedo libre: otro cliente puede tomarlo
        Cita citaLaura = laura.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        System.out.println("Laura toma el horario anterior: " + citaLaura.resumen());

        // Criterio 1: el nuevo horario debe estar disponible
        System.out.println("\n-- Criterio 1: horarios no disponibles --");
        try {
            andres.reprogramarCita(cita, lunes, LocalTime.of(10, 0));
            System.out.println("ERROR: se reprogramo a un horario ocupado");
        } catch (IllegalArgumentException e) {
            System.out.println("OK horario ocupado: " + e.getMessage());
        }
        try {
            andres.reprogramarCita(cita, martes, LocalTime.of(9, 0));
            System.out.println("ERROR: se reprogramo al dia de descanso");
        } catch (IllegalArgumentException e) {
            System.out.println("OK dia de descanso: " + e.getMessage());
        }
        try {
            andres.reprogramarCita(cita, lunes, LocalTime.of(13, 0));
            System.out.println("ERROR: se reprogramo fuera del horario de atencion");
        } catch (IllegalArgumentException e) {
            System.out.println("OK fuera de horario: " + e.getMessage());
        }

        // Criterio 2: con menos de 2 horas el sistema rechaza la reprogramacion
        System.out.println("\n-- Criterio 2: menos de 2 horas --");
        Cita cita2 = andres.agendarCita(kevin, corte, lunes, LocalTime.of(11, 0));
        LocalDateTime enUnaHora = LocalDateTime.now().plusHours(1); // simulamos que la cita es en 1 hora
        cita2.setFecha(enUnaHora.toLocalDate());
        cita2.setHora(enUnaHora.toLocalTime());
        try {
            andres.reprogramarCita(cita2, lunes, LocalTime.of(16, 0));
            System.out.println("ERROR: se reprogramo con menos de 2 horas");
        } catch (IllegalStateException e) {
            System.out.println("OK menos de 2 horas: " + e.getMessage());
        }

        // Otros casos que el sistema debe rechazar
        System.out.println("\n-- Otros casos rechazados --");
        Cita cita3 = andres.agendarCita(kevin, corte, lunes, LocalTime.of(12, 0));
        andres.cancelarCita(cita3);
        try {
            andres.reprogramarCita(cita3, lunes, LocalTime.of(16, 0));
            System.out.println("ERROR: se reprogramo una cita cancelada");
        } catch (IllegalStateException e) {
            System.out.println("OK cita cancelada: " + e.getMessage());
        }
        try {
            andres.reprogramarCita(citaLaura, lunes, LocalTime.of(16, 0));
            System.out.println("ERROR: se reprogramo una cita de otro cliente");
        } catch (IllegalArgumentException e) {
            System.out.println("OK cita ajena: " + e.getMessage());
        }
    }
}