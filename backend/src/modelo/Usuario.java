package modelo;

public abstract class Usuario {

    private static int contador = 1;

    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private String contrasena;

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

    public void editarPerfil() {
        // Lo implementa Yully en HU-01
    }

    public void recuperarContrasena() {
        // Lo implementa Yully en HU-01
    }
}