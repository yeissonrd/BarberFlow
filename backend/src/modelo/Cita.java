package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Cita entre un cliente y un barbero para un servicio, en una fecha y hora.
 * HU que implementa: HU-03 (confirmar y resumen), HU-14 (cancelar) y HU-15 (reprogramar).
 * Los métodos cancelar() y reprogramar() no tienen la regla de las 2 horas:
 * esa regla la valida Cliente, así también los podrán usar el barbero y la administradora.
 */
public class Cita {

    // Contador compartido por todas las citas para asignar un id único y consecutivo
    private static int contador = 1;

    private int id;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado; // "Pendiente" | "Confirmada" | "Cancelada" | "Atendida"
    private Cliente cliente;
    private Barbero barbero;
    private Servicio servicio;

    // Constructor vacío: asigna el id y la cita empieza en estado "Pendiente"
    public Cita() {
        this.id = contador++;
        this.estado = "Pendiente";
    }

    // Sobrecarga del constructor: crea la cita con todos sus datos (HU-03)
    public Cita(Cliente cliente, Barbero barbero, Servicio servicio, LocalDate fecha, LocalTime hora) {
        this();
        this.cliente = cliente;
        this.barbero = barbero;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
    }

    // ---------- Getters y setters ----------

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

    // ---------- HU-03: Agendar una cita ----------

    // HU-03, criterio 2: la cita queda confirmada
    public void confirmar() {
        this.estado = "Confirmada";
    }

    // HU-03, criterio 2: resumen de la cita con fecha, hora, servicio y barbero asignado
    public String resumen() {
        return "Cita #" + id + " | " + fecha + " " + hora
             + " | " + servicio.getNombre()
             + " con " + barbero.getNombre()
             + " | Estado: " + estado;
    }

    // ---------- HU-14: Cancelar una cita ----------

    /**
     * HU-14, criterio 1: calcula cuántas horas faltan desde ahora hasta la cita.
     * Une la fecha y la hora en un LocalDateTime, cuenta los minutos y los pasa a horas con decimales
     * (por ejemplo, 90 minutos = 1.5 horas). Si la cita ya pasó, el resultado es negativo.
     */
    public float horasParaLaCita() {
        LocalDateTime inicio = LocalDateTime.of(fecha, hora);
        long minutos = Duration.between(LocalDateTime.now(), inicio).toMinutes();
        return minutos / 60f;
    }

    // HU-14, criterio 2: el horario vuelve a quedar libre en la agenda del barbero
    public void liberarHorario() {
        barbero.quitarCita(this);
    }

    /**
     * HU-14, criterio 2: cancela la cita y libera el horario.
     * No deja cancelar dos veces la misma cita. La cita sigue en la lista del cliente
     * con estado "Cancelada", para su historial.
     */
    public void cancelar() {
        if (estado.equals("Cancelada")) {
            throw new IllegalStateException("La cita #" + id + " ya estaba cancelada");
        }
        this.estado = "Cancelada";
        liberarHorario();
    }

    // ---------- HU-15: Reprogramar una cita ----------

    /**
     * HU-15, criterio 1: mueve la cita a un nuevo horario disponible del mismo barbero.
     * Como la agenda del barbero guarda este mismo objeto, al cambiar la fecha y la hora
     * el horario anterior queda libre y el nuevo queda ocupado automáticamente.
     * Lo reutilizarán también el barbero (HU-21) y la administradora (HU-27).
     */
    public void reprogramar(LocalDate nuevaFecha, LocalTime nuevaHora) {
        // Una cita cancelada no se puede reprogramar
        if (estado.equals("Cancelada")) {
            throw new IllegalStateException("No se puede reprogramar: la cita #" + id + " esta cancelada");
        }
        // El nuevo horario debe estar disponible para el mismo barbero
        if (!barbero.horariosDisponibles(nuevaFecha).contains(nuevaHora)) {
            throw new IllegalArgumentException(
                "No se puede reprogramar: " + barbero.getNombre() + " no tiene disponible " + nuevaFecha + " a las " + nuevaHora);
        }
        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
    }
}
