import modelo.*;

public class PruebaHU01 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-01 ===");

        // Criterio 1: crear una cuenta de cliente (Frecuente)
        Cliente cliente = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        cliente.registrarse();
        System.out.println("Edad: " + cliente.getEdad());
        System.out.println("Tipo de cliente: " + cliente.getTipoCliente());
        System.out.println("Puntos: " + cliente.getPuntos());

        // Barbero con permisos
        Barbero barbero = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Fade");
        barbero.getPermisos().add("registrar_servicio");
        barbero.registrarse();
        System.out.println("Permisos del barbero: " + barbero.getPermisos());

        // Administrador
        Administrador admin = new Administrador("Sandra Perez", "sandra@ejemplo.com", "3205551234", "clave9999");
        admin.registrarse();

        // Criterio 2: el sistema impide el registro de menores de 15 anos
        try {
            new Cliente("Menor Prueba", "menor@ejemplo.com", "3000000000", "clave0000", 14);
            System.out.println("ERROR: se permitio registrar a un menor de 15");
        } catch (IllegalArgumentException e) {
            System.out.println("Criterio 2 OK: " + e.getMessage());
        }

        // Limite: 15 anos si se permite
        Cliente limite = new Cliente("Limite Prueba", "limite@ejemplo.com", "3000000001", "clave0001", 15);
        System.out.println("Edad 15 permitida: " + limite.getEdad());
    }
}