package modelo;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends Usuario {

    private List<String> permisos;

    public Administrador() {
        super();
        this.permisos = new ArrayList<>();
    }

    // Sobrecarga del constructor
    public Administrador(String nombre, String correo, String telefono, String contrasena, List<String> permisos) {
        super(nombre, correo, telefono, contrasena);
        this.permisos = permisos;
    }

    public List<String> getPermisos() { return permisos; }
    public void setPermisos(List<String> permisos) { this.permisos = permisos; }

    @Override
    public void registrarse() {
        System.out.println("Administrador registrado: " + getNombre() + " (" + getCorreo() + ")");
    }
}
