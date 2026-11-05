package PruebasHU;

import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU26 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-26: Crear citas para cualquier barbero ===");

        Administrador sandra = new Administrador("Sandra Perez", "sandra.hu26@ejemplo.com", "3205551234", "clave9999");
        Cliente andres = new Cliente("Andres Torres", "andres.hu26@ejemplo.com", "3001234567", "clave1234", 27);
        Cliente laura = new Cliente("Laura Mejia", "laura.hu26@ejemplo.com", "3105550101", "clave4321", 31);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin.hu26@ejemplo.com", "3109876543", "clave5678", "Fade");
        Barbero julian = new Barbero("Julian Gomez", "julian.hu26@ejemplo.com", "3101112222", "clave1111", "Barba");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY);
        julian.setDiaDescanso(DayOfWeek.WEDNESDAY);
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre de lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // Criterio 1: la administradora crea citas en la agenda de cualquier barbero
        System.out.println("\n-- Criterio 1: crear citas en la agenda de cualquier barbero --");
        Cita citaKevin = sandra.crearCita(kevin, andres, corte, lunes, LocalTime.of(10, 0));
        Cita citaJulian = sandra.crearCita(julian, laura, corte, lunes, LocalTime.of(15, 0));
        System.out.println(citaKevin.resumen());
        System.out.println(citaJulian.resumen());
        System.out.println("Libres de Kevin el lunes:  " + kevin.horariosDisponibles(lunes));
        System.out.println("Libres de Julian el lunes: " + julian.horariosDisponibles(lunes));

        // Criterio 2: nunca dos citas del mismo barbero en el mismo horario
        System.out.println("\n-- Criterio 2: nunca dos citas del mismo barbero en el mismo horario --");
        try {
            sandra.crearCita(kevin, laura, corte, lunes, LocalTime.of(10, 0));
            System.out.println("ERROR: se asignaron dos citas al mismo barbero en el mismo horario");
        } catch (IllegalArgumentException e) {
            System.out.println("OK horario ocupado: " + e.getMessage());
        }
        System.out.println("Libres de Kevin siguen igual: " + kevin.horariosDisponibles(lunes));

        // La misma hora con otro barbero si se puede
        Cita otraHora = sandra.crearCita(julian, andres, corte, lunes, LocalTime.of(10, 0));
        System.out.println("OK misma hora con otro barbero: " + otraHora.resumen());

        // La cita creada por la administradora queda en la lista del cliente
        System.out.println("\n-- El cliente puede gestionar la cita que creo la administradora --");
        andres.reprogramarCita(citaKevin, lunes, LocalTime.of(16, 0));
        System.out.println("Andres la reprograma: " + citaKevin.resumen());
        andres.cancelarCita(citaKevin);
        System.out.println("Andres la cancela:    " + citaKevin.resumen());

        // Casos que el sistema debe rechazar
        System.out.println("\n-- Casos rechazados --");
        try {
            sandra.crearCita(null, andres, corte, lunes, LocalTime.of(9, 0));
            System.out.println("ERROR: se creo una cita sin barbero");
        } catch (IllegalArgumentException e) {
            System.out.println("OK sin barbero: " + e.getMessage());
        }
        try {
            sandra.crearCita(kevin, null, corte, lunes, LocalTime.of(9, 0));
            System.out.println("ERROR: se creo una cita sin cliente");
        } catch (IllegalArgumentException e) {
            System.out.println("OK sin cliente: " + e.getMessage());
        }
        try {
            sandra.crearCita(kevin, andres, null, lunes, LocalTime.of(9, 0));
            System.out.println("ERROR: se creo una cita sin servicio");
        } catch (IllegalArgumentException e) {
            System.out.println("OK sin servicio: " + e.getMessage());
        }
        try {
            sandra.crearCita(kevin, andres, corte, null, LocalTime.of(9, 0));
            System.out.println("ERROR: se creo una cita sin fecha");
        } catch (IllegalArgumentException e) {
            System.out.println("OK sin fecha: " + e.getMessage());
        }
        try {
            sandra.crearCita(kevin, andres, corte, lunes, null);
            System.out.println("ERROR: se creo una cita sin hora");
        } catch (IllegalArgumentException e) {
            System.out.println("OK sin hora: " + e.getMessage());
        }
        try {
            sandra.crearCita(kevin, andres, corte, lunes.plusDays(1), LocalTime.of(9, 0));
            System.out.println("ERROR: se creo una cita en el dia de descanso");
        } catch (IllegalArgumentException e) {
            System.out.println("OK dia de descanso: " + e.getMessage());
        }
        try {
            sandra.crearCita(kevin, andres, corte, lunes, LocalTime.of(13, 0));
            System.out.println("ERROR: se creo una cita fuera del horario de atencion");
        } catch (IllegalArgumentException e) {
            System.out.println("OK fuera de horario: " + e.getMessage());
        }
    }
}
