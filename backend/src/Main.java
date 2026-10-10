import modelo.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Backend BarberFlow listo");

        // Prueba del criterio 1: crear una cuenta de cliente
        Cliente cliente = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        cliente.registrarse();
        System.out.println("Edad: " + cliente.getEdad());

        // Prueba de Barbero
        Barbero barbero = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Fade");
        barbero.registrarse();

        // Prueba de Administrador
        List<String> permisos = new ArrayList<>();
        permisos.add("gestionar_clientes");
        permisos.add("ver_panel_financiero");
        Administrador admin = new Administrador("Sandra Perez", "sandra@ejemplo.com", "3205551234", "clave9999", permisos);
        admin.registrarse();
        System.out.println("Permisos: " + admin.getPermisos());
    }
}
