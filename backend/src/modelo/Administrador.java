package modelo;

public class Administrador extends Usuario {

    public Administrador() {
        super();
    }

    // Sobrecarga del constructor
    public Administrador(String nombre, String correo, String telefono, String contrasena) {
        super(nombre, correo, telefono, contrasena);
    }

       @Override
    public void registrarse() {
        registrarCorreo();
        System.out.println("Administrador registrado: " + getNombre() + " (" + getCorreo() + ")");
    }
}