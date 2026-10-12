package modelo;

public class Servicio {

    private static int contador = 1;

    private int id;
    private String nombre;
    private String tipo;
    private float precio;
    private int puntosOtorgados;

    public Servicio(String nombre, String tipo, float precio) {
        this.id = contador++;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
    }

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public float getPrecio() { return precio; }
    public void setPrecio(float precio) { this.precio = precio; }

    public int calcularPuntos() {
        // Lo implementa quien haga HU-06
        return puntosOtorgados;
    }
}