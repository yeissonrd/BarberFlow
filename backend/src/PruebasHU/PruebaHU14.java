package PruebasHU;

import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU14 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-14: Cancelar una cita ===");

        Cliente andres = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        Cliente laura = new Cliente("Laura Mejia", "laura@ejemplo.com", "3105550101", "clave4321", 31);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Barba y perfilado");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY);
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre de lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // Criterio 1 y 2: cancelar con mas de 2 horas de anticipacion libera el horario
        System.out.println("\n-- Criterio 1 y 2: cancelacion permitida --");
        Cita cita = andres.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        System.out.println(cita.resumen());
        System.out.println("Libres antes de cancelar:  " + kevin.horariosDisponibles(lunes));
        andres.cancelarCita(cita);
        System.out.println(cita.resumen());
        System.out.println("Libres despues de cancelar: " + kevin.horariosDisponibles(lunes));

        // Criterio 2: otro cliente ya puede tomar ese horario
        Cita citaLaura = laura.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        System.out.println("Laura toma el horario liberado: " + citaLaura.resumen());

        // Criterio 1: con menos de 2 horas el sistema rechaza la cancelacion
        System.out.println("\n-- Criterio 1: cancelacion rechazada --");
        Cita cita2 = andres.agendarCita(kevin, corte, lunes, LocalTime.of(11, 0));
        LocalDateTime enUnaHora = LocalDateTime.now().plusHours(1); // simulamos que la cita es en 1 hora
        cita2.setFecha(enUnaHora.toLocalDate());
        cita2.setHora(enUnaHora.toLocalTime());
        try {
            andres.cancelarCita(cita2);
            System.out.println("ERROR: se cancelo con menos de 2 horas");
        } catch (IllegalStateException e) {
            System.out.println("OK menos de 2 horas: " + e.getMessage());
        }

        // Otros casos que el sistema debe rechazar
        System.out.println("\n-- Otros casos rechazados --");
        try {
            andres.cancelarCita(cita);
            System.out.println("ERROR: se cancelo dos veces la misma cita");
        } catch (IllegalStateException e) {
            System.out.println("OK ya cancelada: " + e.getMessage());
        }
        try {
            andres.cancelarCita(citaLaura);
            System.out.println("ERROR: se cancelo una cita de otro cliente");
        } catch (IllegalArgumentException e) {
            System.out.println("OK cita ajena: " + e.getMessage());
        }
    }
}