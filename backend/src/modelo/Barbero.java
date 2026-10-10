package modelo;

import java.util.ArrayList;
import java.util.List;

public class Barbero extends Usuario {

    private String especialidad;
    private float comision;
    private List<String> permisos = new ArrayList<>(); // asignados por la administradora

    public Barbero() {
        super();
    }

    // Sobrecarga del constructor
    public Barbero(String nombre, String correo, String telefono, String contrasena, String especialidad) {
        super(nombre, correo, telefono, contrasena);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public float getComision() { return comision; }
    public void setComision(float comision) { this.comision = comision; }

    public List<String> getPermisos() { return permisos; }
    public void setPermisos(List<String> permisos) { this.permisos = permisos; }

    @Override
    public void registrarse() {
        System.out.println("Barbero registrado: " + getNombre() + " (" + getEspecialidad() + ")");
    }
}