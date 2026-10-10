import modelo.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Backend BarberFlow listo");

        // Prueba del criterio 1: crear una cuenta de cliente
        Cliente cliente = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        cliente.registrarse();
        System.out.println("Edad: " + cliente.getEdad());
    }
}
