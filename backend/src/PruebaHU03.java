import modelo.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class PruebaHU03 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-03 ===");

        Cliente andres = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        Barbero kevin = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Barba y perfilado");
        Servicio corte = new Servicio("Corte de cabello", "corte", 25000);

        Cita cita = new Cita(andres, kevin, corte, LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        System.out.println("Antes de confirmar: " + cita.getEstado());

        cita.confirmar();
        System.out.println(cita.resumen());
    }
}