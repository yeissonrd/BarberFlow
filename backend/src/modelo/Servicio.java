package modelo;

/**
 * Servicio que ofrece la barbería (por ejemplo, corte de cabello o barba).
 * HU-03: cada cita corresponde a un servicio.
 */
public class Servicio {

    // Contador compartido para asignar un id único y consecutivo a cada servicio
    private static int contador = 1;

    private int id;
    private String nombre;
    private String tipo;
    private float precio;
    private int puntosOtorgados; // puntos que gana el cliente Frecuente (se usará en HU-06)

    // Constructor: crea el servicio con su nombre, tipo y precio
    public Servicio(String nombre, String tipo, float precio) {
        this.id = contador++;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
    }

    // ---------- Getters y setters ----------

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public float getPrecio() { return precio; }
    public void setPrecio(float precio) { this.precio = precio; }

    // HU-06 (pendiente): calculará los puntos que gana el cliente por este servicio
    public int calcularPuntos() {
        // Lo implementa quien haga HU-06
        return puntosOtorgados;
    }
}
