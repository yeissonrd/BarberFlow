package modelo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Barbero de la barbería. Hereda de Usuario (extends) los datos personales y el registro.
 * Maneja su agenda de citas y calcula sus horarios disponibles.
 * HU que implementa: HU-01 (registro), HU-03 (horarios disponibles), HU-14 (liberar horario)
 * y HU-25 (consultar sus permisos).
 */
public class Barbero extends Usuario {

    private String especialidad;
    private float comision;
    private List<String> permisos = new ArrayList<>(); // asignados por la administradora (HU-25)
    private DayOfWeek diaDescanso;                     // un día por semana, lo asigna la administradora (HU-30)
    private List<Cita> agenda = new ArrayList<>();      // citas que atiende este barbero

    // Constructor vacío
    public Barbero() {
        super();
    }

    // Sobrecarga del constructor: barbero con sus datos personales y su especialidad
    public Barbero(String nombre, String correo, String telefono, String contrasena, String especialidad) {
        super(nombre, correo, telefono, contrasena);
        this.especialidad = especialidad;
    }

    // ---------- Getters y setters ----------

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public float getComision() { return comision; }
    public void setComision(float comision) { this.comision = comision; }

    public List<String> getPermisos() { return permisos; }
    public void setPermisos(List<String> permisos) { this.permisos = permisos; }

    public DayOfWeek getDiaDescanso() { return diaDescanso; }
    public void setDiaDescanso(DayOfWeek diaDescanso) { this.diaDescanso = diaDescanso; }

    /**
     * HU-25, criterio 1: dice si el barbero tiene un permiso asignado en su perfil.
     * No distingue mayusculas de minusculas.
     */
    public boolean tienePermiso(String permiso) {
        if (permiso == null || permisos == null) {
            return false;
        }
        for (String asignado : permisos) {
            if (asignado.equalsIgnoreCase(permiso.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * HU-01: registra al barbero en la plataforma.
     * Lleva @Override porque es la versión del Barbero del método abstracto de Usuario.
     */
    @Override
    public void registrarse() {
        registrarCorreo();
        System.out.println("Barbero registrado: " + getNombre() + " (" + getEspecialidad() + ")");
    }

    // ---------- Agenda (HU-03 y HU-14) ----------

    // HU-03, criterio 2: guarda la cita confirmada en la agenda de este barbero
    public void agregarCita(Cita cita) {
        agenda.add(cita);
    }

    // HU-14, criterio 2: quita la cita de la agenda para que ese horario vuelva a quedar libre
    public void quitarCita(Cita cita) {
        agenda.remove(cita);
    }

    /**
     * HU-03, criterio 1: devuelve solo los horarios libres del barbero en una fecha.
     * Parte del horario de atención de la barbería y descarta los que no se pueden ofrecer.
     * También lo usa HU-15 (reprogramar) para validar el nuevo horario.
     */
    public List<LocalTime> horariosDisponibles(LocalDate fecha) {
        List<LocalTime> libres = new ArrayList<>();

        // Regla 1: el día de descanso del barbero no tiene horarios (lista vacía)
        if (fecha.getDayOfWeek() == diaDescanso) {
            return libres;
        }

        // Se recorre el horario de atención vigente de la barbería
        for (LocalTime hora : Barberia.getHorarioAtencion()) {
            // Regla 2: si la fecha es hoy, no se ofrecen horas que ya pasaron
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

    // HU-03, criterio 1: revisa si ya hay una cita en la agenda para esa fecha y hora.
    // Es privado porque solo lo usa horariosDisponibles().
    private boolean estaOcupado(LocalDate fecha, LocalTime hora) {
        for (Cita cita : agenda) {
            if (cita.getFecha().equals(fecha) && cita.getHora().equals(hora)) {
                return true;
            }
        }
        return false;
    }
}