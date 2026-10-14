package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {

    private static final int EDAD_MINIMA = 15;
    private static final int HORAS_MINIMAS_CANCELAR = 2; // HU-14

    private int edad;
    private String tipoCliente; // "Frecuente" | "No frecuente"
    private int puntos;
    private List<Cita> citas = new ArrayList<>(); // citas agendadas por este cliente

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
        registrarCorreo();
        System.out.println("Cliente registrado: " + getNombre() + " (" + getCorreo() + ")");
    }

    // HU-03: el cliente agenda una cita con un barbero en un horario disponible
    public Cita agendarCita(Barbero barbero, Servicio servicio, LocalDate fecha, LocalTime hora) {
        // Criterio 1: solo se puede agendar en un horario disponible del barbero
        if (!barbero.horariosDisponibles(fecha).contains(hora)) {
            throw new IllegalArgumentException(
                "No se puede agendar: " + barbero.getNombre() + " no tiene disponible " + fecha + " a las " + hora);
        }

        // Criterio 2: se crea la cita con fecha, hora, servicio y barbero, y queda confirmada
        Cita cita = new Cita(this, barbero, servicio, fecha, hora);
        barbero.agregarCita(cita);
        citas.add(cita);
        cita.confirmar();
        return cita;
    }
        // HU-14: el cliente cancela una de sus citas
    public void cancelarCita(Cita cita) {
        // Solo puede cancelar sus propias citas
        if (!citas.contains(cita)) {
            throw new IllegalArgumentException(
                "No se puede cancelar: la cita #" + cita.getId() + " no pertenece a " + getNombre());
        }
        // Criterio 1: solo se permite cancelar hasta 2 horas antes
        if (cita.horasParaLaCita() < HORAS_MINIMAS_CANCELAR) {
            throw new IllegalStateException(
                "No se puede cancelar: faltan menos de " + HORAS_MINIMAS_CANCELAR + " horas para la cita");
        }
        // Criterio 2: la cita queda cancelada y el horario se libera
        cita.cancelar();
    }
}