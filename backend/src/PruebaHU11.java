import modelo.*;

public class PruebaHU11 {
    public static void main(String[] args) {
        System.out.println("=== Prueba HU-11 ===");

        Cliente cliente = new Cliente("Andres Torres", "andres@ejemplo.com", "3001234567", "clave1234", 27);
        cliente.registrarse();
        Barbero barbero = new Barbero("Kevin Ramirez", "kevin@ejemplo.com", "3109876543", "clave5678", "Fade");
        barbero.registrarse();

        // Criterio 1: editar nombre y telefono; el correo no cambia
        cliente.editarPerfil("Andres Felipe Torres", null, "3005550000");
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("Telefono: " + cliente.getTelefono());
        System.out.println("Correo sin cambios: " + cliente.getCorreo());

        // Cambiar el correo a uno nuevo
        cliente.editarPerfil(null, "andres.nuevo@ejemplo.com", null);
        System.out.println("Correo nuevo: " + cliente.getCorreo());

        // El correo anterior queda libre para otra cuenta
        Cliente otro = new Cliente("Otro Cliente", "andres@ejemplo.com", "3001112222", "clave0000", 20);
        otro.registrarse();

        // Un correo ya usado por otra cuenta se rechaza
        try {
            barbero.editarPerfil(null, "ANDRES.NUEVO@ejemplo.com", null);
            System.out.println("ERROR: se permitio un correo repetido");
        } catch (IllegalArgumentException e) {
            System.out.println("Correo repetido OK: " + e.getMessage());
        }
        System.out.println("Correo del barbero sigue igual: " + barbero.getCorreo());

        // Un nombre vacio se rechaza
        try {
            cliente.editarPerfil("   ", null, null);
            System.out.println("ERROR: se permitio un nombre vacio");
        } catch (IllegalArgumentException e) {
            System.out.println("Nombre vacio OK: " + e.getMessage());
        }
        System.out.println("Nombre sigue igual: " + cliente.getNombre());

        // Un correo vacio se rechaza
        try {
            cliente.editarPerfil(null, "  ", null);
            System.out.println("ERROR: se permitio un correo vacio");
        } catch (IllegalArgumentException e) {
            System.out.println("Correo vacio OK: " + e.getMessage());
        }

        // La edicion tambien funciona en otro rol
        barbero.editarPerfil("Kevin Ramirez Gomez", null, null);
        System.out.println("Barbero editado: " + barbero.getNombre());

        // Criterio 2: recuperar la contrasena mediante un enlace enviado al correo
        cliente.recuperarContrasena();
        String enlace = cliente.getEnlaceRecuperacion();
        System.out.println("Clave actual antes: " + cliente.verificarContrasena("clave1234"));

        // Un enlace falso se rechaza
        try {
            cliente.restablecerContrasena("https://barberflow.com/recuperar?token=falso", "claveNueva99");
            System.out.println("ERROR: se acepto un enlace falso");
        } catch (IllegalArgumentException e) {
            System.out.println("Enlace invalido OK: " + e.getMessage());
        }

        // Con el enlace correcto la clave cambia
        cliente.restablecerContrasena(enlace, "claveNueva99");
        System.out.println("Clave nueva aceptada: " + cliente.verificarContrasena("claveNueva99"));
        System.out.println("Clave anterior ya no sirve: " + !cliente.verificarContrasena("clave1234"));

        // El enlace solo sirve una vez
        try {
            cliente.restablecerContrasena(enlace, "otraClave");
            System.out.println("ERROR: se reutilizo un enlace");
        } catch (IllegalArgumentException e) {
            System.out.println("Enlace de un solo uso OK: " + e.getMessage());
        }

        // Una contrasena vacia se rechaza
        cliente.recuperarContrasena();
        try {
            cliente.restablecerContrasena(cliente.getEnlaceRecuperacion(), "   ");
            System.out.println("ERROR: se acepto una contrasena vacia");
        } catch (IllegalArgumentException e) {
            System.out.println("Contrasena vacia OK: " + e.getMessage());
        }

        // Una cuenta que no se registro no puede recuperar contrasena
        Cliente sinCuenta = new Cliente("Sin Cuenta", "sincuenta@ejemplo.com", "3000000009", "clave0009", 30);
        try {
            sinCuenta.recuperarContrasena();
            System.out.println("ERROR: se envio un enlace a un correo no registrado");
        } catch (IllegalStateException e) {
            System.out.println("Cuenta sin registrar OK: " + e.getMessage());
        }
    }
}