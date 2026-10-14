package modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Barbero extends Usuario {

    private String especialidad;
    private float comision;
    private List<String> permisos = new ArrayList<>(); // asignados por la administradora
    private DayOfWeek diaDescanso;                     // lo asigna la administradora (HU-30)
    private List<Cita> agenda = new ArrayList<>();      // citas que atiende este barbero

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

    public List<String> getPermisos() { return permisos; }
    public void setPermisos(List<String> permisos) { this.permisos = permisos; }

    public DayOfWeek getDiaDescanso() { return diaDescanso; }
    public void setDiaDescanso(DayOfWeek diaDescanso) { this.diaDescanso = diaDescanso; }

    @Override
    public void registrarse() {
        registrarCorreo();
        System.out.println("Barbero registrado: " + getNombre() + " (" + getEspecialidad() + ")");
    }

    // HU-03: guarda una cita en la agenda de este barbero
    public void agregarCita(Cita cita) {
        agenda.add(cita);
    }
    // HU-14, criterio 2: quita la cita de la agenda para que el horario quede libre
    public void quitarCita(Cita cita) {
        agenda.remove(cita);
    }

    // HU-03, criterio 1: horarios libres del barbero en una fecha
    public List<LocalTime> horariosDisponibles(LocalDate fecha) {
        List<LocalTime> libres = new ArrayList<>();

        // Regla 1: el día de descanso del barbero no tiene horarios
        if (fecha.getDayOfWeek() == diaDescanso) {
            return libres;
        }

        // Se recorre el horario de atención vigente de la barbería
        for (LocalTime hora : Barberia.getHorarioAtencion()) {
            // Regla 2: si es hoy, no se ofrecen horas que ya pasaron
            if (fecha.equals(LocalDate.now()) && !hora.isAfter(LocalTime.now())) {
                continue;
            }
            // Regla 3: no se ofrecen horas que ya tienen una cita
            if (!estaOcupado(fecha, hora)) {
                libres.add(hora);
            }
        }
        return libres;
    }

    // Revisa si ya hay una cita en la agenda para esa fecha y hora
    private boolean estaOcupado(LocalDate fecha, LocalTime hora) {
        for (Cita cita : agenda) {
            if (cita.getFecha().equals(fecha) && cita.getHora().equals(hora)) {
                return true;
            }
        }
        return false;
    }
}