package PruebasHU;

import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU30 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-30: Asignar el dia de descanso de cada barbero ===");

        Administrador sandra = new Administrador("Sandra Perez", "sandra.hu30@ejemplo.com", "3205551234", "clave9999");
        Cliente andres = new Cliente("Andres Torres", "andres.hu30@ejemplo.com", "3001234567", "clave1234", 27);
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Semana futura, para que la prueba siempre dé lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        LocalDate martes = lunes.plusDays(1);
        LocalDate miercoles = lunes.plusDays(2);
        LocalDate jueves = lunes.plusDays(3);

        // Criterio 1: al registrar al barbero se le asigna un único día de descanso
        System.out.println("\n-- Criterio 1: asignar el descanso al registrar --");
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin.hu30@ejemplo.com", "3109876543", "clave5678", "Fade");
        kevin.registrarse();
        System.out.println("Descanso antes de asignar: " + kevin.getDiaDescanso());
        sandra.asignarDiaDescanso(kevin, DayOfWeek.TUESDAY);
        System.out.println("Descanso asignado: " + kevin.getDiaDescanso());

        // Criterio 3: en su día de descanso no tiene horarios disponibles
        System.out.println("\n-- Criterio 3: sin horarios el dia de descanso --");
        System.out.println("Libres el lunes: " + kevin.horariosDisponibles(lunes));
        System.out.println("Libres el martes (descanso): " + kevin.horariosDisponibles(martes));
        try {
            andres.agendarCita(kevin, corte, martes, LocalTime.of(10, 0));
            System.out.println("ERROR: se agendo en el dia de descanso");
        } catch (IllegalArgumentException e) {
            System.out.println("OK no se agenda en el descanso: " + e.getMessage());
        }

        // Criterio 2: Kevin pide cambiar su descanso al miércoles
        System.out.println("\n-- Criterio 2: cambiar el descanso a peticion del barbero --");
        sandra.asignarDiaDescanso(kevin, DayOfWeek.WEDNESDAY);
        System.out.println("Nuevo descanso: " + kevin.getDiaDescanso());
        System.out.println("Libres el martes (ya trabaja): " + kevin.horariosDisponibles(martes));
        System.out.println("Libres el miercoles (descanso): " + kevin.horariosDisponibles(miercoles));

        // Criterio 2: no se puede cambiar a un día en el que ya tiene citas
        System.out.println("\n-- Criterio 2: dia con citas agendadas --");
        Cita citaJueves = andres.agendarCita(kevin, corte, jueves, LocalTime.of(10, 0));
        try {
            sandra.asignarDiaDescanso(kevin, DayOfWeek.THURSDAY);
            System.out.println("ERROR: se asigno el descanso en un dia con citas");
        } catch (IllegalStateException e) {
            System.out.println("OK dia con citas: " + e.getMessage());
        }
        System.out.println("El descanso sigue siendo: " + kevin.getDiaDescanso());

        // Si la administradora cancela esa cita (HU-28), ya puede hacer el cambio
        sandra.cancelarCita(citaJueves);
        sandra.asignarDiaDescanso(kevin, DayOfWeek.THURSDAY);
        System.out.println("OK despues de cancelar la cita, nuevo descanso: " + kevin.getDiaDescanso());

        // Casos que el sistema debe rechazar
        System.out.println("\n-- Casos rechazados --");
        try {
            sandra.asignarDiaDescanso(null, DayOfWeek.MONDAY);
            System.out.println("ERROR: se acepto un barbero nulo");
        } catch (IllegalArgumentException e) {
            System.out.println("OK barbero nulo: " + e.getMessage());
        }
        try {
            sandra.asignarDiaDescanso(kevin, null);
            System.out.println("ERROR: se acepto un dia nulo");
        } catch (IllegalArgumentException e) {
            System.out.println("OK dia nulo: " + e.getMessage());
        }
        System.out.println("El descanso sigue siendo: " + kevin.getDiaDescanso());
    }
}
