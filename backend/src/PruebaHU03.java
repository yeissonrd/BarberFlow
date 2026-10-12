import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU03 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-03: Agendar una cita ===");

        Cliente andres = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        Cliente laura = new Cliente("Laura Mejia", "laura@ejemplo.com", "3105550101", "clave4321", 31);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Barba y perfilado");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY); // en HU-30 lo asignará la administradora
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre dé lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate martes = lunes.plusDays(1);

        // Criterio 1: el sistema muestra solo los horarios disponibles del barbero
        System.out.println("\n-- Criterio 1 --");
        System.out.println("Horarios libres de Kevin el " + lunes + ": " + kevin.horariosDisponibles(lunes));
        System.out.println("Martes " + martes + " (descanso): " + kevin.horariosDisponibles(martes));

        // Criterio 2: el sistema confirma la cita con fecha, hora, servicio y barbero
        System.out.println("\n-- Criterio 2 --");
        Cita cita = andres.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
        System.out.println(cita.resumen());
        System.out.println("Horarios libres despues de agendar: " + kevin.horariosDisponibles(lunes));

        // Casos que el sistema debe rechazar
        System.out.println("\n-- Casos rechazados --");
        try {
            laura.agendarCita(kevin, corte, lunes, LocalTime.of(10, 0));
            System.out.println("ERROR: se agendo un horario ocupado");
        } catch (IllegalArgumentException e) {
            System.out.println("OK horario ocupado: " + e.getMessage());
        }
        try {
            laura.agendarCita(kevin, corte, martes, LocalTime.of(9, 0));
            System.out.println("ERROR: se agendo en el dia de descanso");
        } catch (IllegalArgumentException e) {
            System.out.println("OK dia de descanso: " + e.getMessage());
        }
        try {
            laura.agendarCita(kevin, corte, lunes, LocalTime.of(13, 0));
            System.out.println("ERROR: se agendo fuera del horario de atencion");
        } catch (IllegalArgumentException e) {
            System.out.println("OK fuera de horario: " + e.getMessage());
        }
    }
}