package modelo;

/**
 * Administradora de la barbería. Hereda de Usuario (extends) los datos personales y el registro.
 * Por ahora no tiene atributos propios: sus funciones (gestionar barberos, servicios,
 * horarios y todas las agendas) llegan en los siguientes sprints.
 */
public class Administrador extends Usuario {

    // Constructor vacío
    public Administrador() {
        super();
    }

    // Sobrecarga del constructor: administradora con sus datos personales
    public Administrador(String nombre, String correo, String telefono, String contrasena) {
        super(nombre, correo, telefono, contrasena);
    }

    /**
     * HU-01: registra a la administradora en la plataforma.
     * Lleva @Override porque es la versión del Administrador del método abstracto de Usuario.
     */
    @Override
    public void registrarse() {
        registrarCorreo();
        System.out.println("Administrador registrado: " + getNombre() + " (" + getCorreo() + ")");
    }
}
