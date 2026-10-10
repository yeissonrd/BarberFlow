package modelo;

public class Barbero extends Usuario {

    private String especialidad;
    private float comision;

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

    @Override
    public void registrarse() {
        System.out.println("Barbero registrado: " + getNombre() + " (" + getEspecialidad() + ")");
    }
}
