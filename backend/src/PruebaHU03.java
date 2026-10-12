import modelo.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class PruebaHU03 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-03 ===");

        Cliente andres = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Barba y perfilado");
        kevin.setDiaDescanso(DayOfWeek.TUESDAY); // en HU-30 lo asignará la administradora
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        // Un lunes futuro, para que la prueba siempre dé lo mismo
        LocalDate lunes = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        // Criterio 1: horarios disponibles del barbero
        System.out.println("Horarios libres de Kevin el " + lunes + ": " + kevin.horariosDisponibles(lunes));

        Cita cita = new Cita(andres, kevin, corte, lunes, LocalTime.of(10, 0));
        kevin.agregarCita(cita);
        System.out.println("Despues de agendar las 10:00: " + kevin.horariosDisponibles(lunes));

        LocalDate martes = lunes.plusDays(1);
        System.out.println("Martes " + martes + " (descanso de Kevin): " + kevin.horariosDisponibles(martes));

        LocalDate domingo = lunes.minusDays(1);
        System.out.println("Domingo " + domingo + ": " + kevin.horariosDisponibles(domingo));

        // Criterio 2 (paso 3): confirmar la cita
        cita.confirmar();
        System.out.println(cita.resumen());
    }
}