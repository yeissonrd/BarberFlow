package modelo;

public class Cliente extends Usuario {

    private int edad;
    private String tipoCliente; // "Frecuente" | "No frecuente"
    private int puntos;

    public Cliente() {
        super();
    }

    // Sobrecarga del constructor: cliente registrado en la plataforma
    public Cliente(String nombre, String correo, String telefono, String contrasena, int edad) {
        super(nombre, correo, telefono, contrasena);
        this.edad = edad;
        this.tipoCliente = "Frecuente";
        this.puntos = 0;
    }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    @Override
    public void registrarse() {
        System.out.println("Cliente registrado: " + getNombre() + " (" + getCorreo() + ")");
    }
}