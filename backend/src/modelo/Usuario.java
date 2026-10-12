package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Usuario {

    private static int contador = 1;

    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private String contrasena;
    private String enlaceRecuperacion; // HU-11, criterio 2: enlace vigente de recuperacion

    public Usuario() {
        this.id = contador++;
    }

    // Sobrecarga del constructor
    public Usuario(String nombre, String correo, String telefono, String contrasena) {
        this();
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public abstract void registrarse();

    // HU-11, criterio 1: el sistema permite editar los datos del perfil en cualquier momento.
    // Un dato en null no se modifica.
    public void editarPerfil(String nuevoNombre, String nuevoCorreo, String nuevoTelefono) {
        if (nuevoNombre != null && nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Edicion no permitida: el nombre no puede estar vacio");
        }

        String correoNormalizado = null;
        if (nuevoCorreo != null) {
            if (nuevoCorreo.trim().isEmpty()) {
                throw new IllegalArgumentException("Edicion no permitida: el correo no puede estar vacio");
            }
            correoNormalizado = nuevoCorreo.trim().toLowerCase();
            String anterior = (correo == null) ? "" : correo.trim().toLowerCase();
            if (!correoNormalizado.equals(anterior) && correosRegistrados.contains(correoNormalizado)) {
                throw new IllegalArgumentException("Edicion no permitida: el correo ya esta registrado");
            }
        }

        // Todo validado: se aplican los cambios
        if (nuevoNombre != null) {
            this.nombre = nuevoNombre.trim();
        }
        if (nuevoCorreo != null) {
            String anterior = (correo == null) ? "" : correo.trim().toLowerCase();
            if (!correoNormalizado.equals(anterior)) {
                // Si la cuenta ya estaba registrada, se actualiza la lista de correos
                if (correosRegistrados.remove(anterior)) {
                    correosRegistrados.add(correoNormalizado);
                }
            }
            this.correo = nuevoCorreo.trim();
        }
        if (nuevoTelefono != null) {
            this.telefono = nuevoTelefono.trim();
        }
    }

    // HU-11, criterio 2: el sistema permite recuperar la contrasena mediante un enlace
    // enviado al correo registrado. El envio se simula imprimiendo el enlace en consola.
    public void recuperarContrasena() {
        if (correo == null || !correosRegistrados.contains(correo.trim().toLowerCase())) {
            throw new IllegalStateException("Recuperacion no permitida: el correo no esta registrado");
        }
        enlaceRecuperacion = "https://barberflow.com/recuperar?token=" + UUID.randomUUID();
        System.out.println("Enlace de recuperacion enviado a " + correo + ": " + enlaceRecuperacion);
    }

    public String getEnlaceRecuperacion() { return enlaceRecuperacion; }

    // Simula que la persona abre el enlace y escribe su nueva contrasena.
    // El enlace solo sirve una vez.
    public void restablecerContrasena(String enlace, String nuevaContrasena) {
        if (enlaceRecuperacion == null || !enlaceRecuperacion.equals(enlace)) {
            throw new IllegalArgumentException("Recuperacion no permitida: el enlace no es valido");
        }
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Recuperacion no permitida: la contrasena no puede estar vacia");
        }
        this.contrasena = nuevaContrasena;
        this.enlaceRecuperacion = null;
    }

    // Permite comprobar una contrasena sin exponerla con un getter
    public boolean verificarContrasena(String intento) {
        return contrasena != null && contrasena.equals(intento);
    }

    // HU-01, criterio 3: correos ya registrados en el sistema
    private static final List<String> correosRegistrados = new ArrayList<>();

    // Valida que el correo no este registrado y lo guarda. Se llama desde registrarse()
    protected void registrarCorreo() {
        String correoActual = getCorreo();
        if (correoActual == null || correoActual.trim().isEmpty()) {
            throw new IllegalArgumentException("Registro no permitido: el correo es obligatorio");
        }
        String normalizado = correoActual.trim().toLowerCase();
        if (correosRegistrados.contains(normalizado)) {
            throw new IllegalArgumentException("Registro no permitido: el correo ya esta registrado");
        }
        correosRegistrados.add(normalizado);
    }
}