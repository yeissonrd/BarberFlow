package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.LocalDateTime;

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
        // HU-14, criterio 1: horas que faltan desde ahora hasta la cita
    public float horasParaLaCita() {
        LocalDateTime inicio = LocalDateTime.of(fecha, hora);
        long minutos = Duration.between(LocalDateTime.now(), inicio).toMinutes();
        return minutos / 60f;
    }

    // HU-14, criterio 2: el horario vuelve a quedar libre en la agenda del barbero
    public void liberarHorario() {
        barbero.quitarCita(this);
    }

    // HU-14: cambia el estado y libera el horario
    // La regla de las 2 horas la valida Cliente, porque barbero y admin cancelan en cualquier momento
    public void cancelar() {
        if (estado.equals("Cancelada")) {
            throw new IllegalStateException("La cita #" + id + " ya estaba cancelada");
        }
        this.estado = "Cancelada";
        liberarHorario();
    }
        // HU-15, criterio 1: mueve la cita a un nuevo horario disponible del mismo barbero
    // Lo reutilizan tambien el barbero (HU-21) y la administradora (HU-27)
    public void reprogramar(LocalDate nuevaFecha, LocalTime nuevaHora) {
        if (estado.equals("Cancelada")) {
            throw new IllegalStateException("No se puede reprogramar: la cita #" + id + " esta cancelada");
        }
        if (!barbero.horariosDisponibles(nuevaFecha).contains(nuevaHora)) {
            throw new IllegalArgumentException(
                "No se puede reprogramar: " + barbero.getNombre() + " no tiene disponible " + nuevaFecha + " a las " + nuevaHora);
        }
        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
    }
}
