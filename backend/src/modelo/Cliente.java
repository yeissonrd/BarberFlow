package modelo;

public class Cliente extends Usuario {

    private static final int EDAD_MINIMA = 15;

    private int edad;
    private String tipoCliente; // "Frecuente" | "No frecuente"
    private int puntos;

    public Cliente() {
        super();
    }

    // Sobrecarga del constructor: cliente registrado en la plataforma
    public Cliente(String nombre, String correo, String telefono, String contrasena, int edad) {
        super(nombre, correo, telefono, contrasena);
        validarEdad(edad);
        this.edad = edad;
        this.tipoCliente = "Frecuente";
        this.puntos = 0;
    }

    // HU-01, criterio 2: el sistema impide el registro de menores de 15 anos
    private static void validarEdad(int edad) {
        if (edad < EDAD_MINIMA) {
            throw new IllegalArgumentException(
                "Registro no permitido: la edad minima es de " + EDAD_MINIMA + " anos");
        }
    }

    public int getEdad() { return edad; }

    public void setEdad(int edad) {
        validarEdad(edad);
        this.edad = edad;
    }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    @Override
    public void registrarse() {
        System.out.println("Cliente registrado: " + getNombre() + " (" + getCorreo() + ")");
    }
}