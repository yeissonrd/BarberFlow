package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private static int contador = 1;

    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado; // "Pendiente" | "Confirmada" | "Cancelada" | "Atendida"
    private Cliente cliente;
    private Barbero barbero;
    private Servicio servicio;

    public Cita() {
        this.id = contador++;
        this.estado = "Pendiente";
    }

    // Sobrecarga del constructor
    public Cita(Cliente cliente, Barbero barbero, Servicio servicio, LocalDate fecha, LocalTime hora) {
        this();
        this.cliente = cliente;
        this.barbero = barbero;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
    }

    public int getId() { return id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Cliente getCliente() { return cliente; }
    public Barbero getBarbero() { return barbero; }
    public Servicio getServicio() { return servicio; }

       // HU-03, criterio 2
    public void confirmar() {
        this.estado = "Confirmada";
    }
    
    // HU-03, criterio 2: resumen con fecha, hora, servicio y barbero
    public String resumen() {
        return "Cita #" + id + " | " + fecha + " " + hora
             + " | " + servicio.getNombre()
             + " con " + barbero.getNombre()
             + " | Estado: " + estado;
    }
}