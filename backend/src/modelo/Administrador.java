package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Administradora de la barberia. Hereda de Usuario (extends) los datos personales y el registro.
 * Por ahora no tiene atributos propios: sus funciones (gestionar barberos, servicios,
 * horarios y todas las agendas) llegan en los siguientes sprints.
 * HU que implementa: HU-01 (registro), HU-25 (asignar permisos a los barberos),
 * HU-28 (cancelar cualquier cita), HU-27 (reprogramar cualquier cita)
 * y HU-26 (crear citas para cualquier barbero).
 */
public class Administrador extends Usuario {

    // Constructor vacio
    public Administrador() {
        super();
    }

    // Sobrecarga del constructor: administradora con sus datos personales
    public Administrador(String nombre, String correo, String telefono, String contrasena) {
        super(nombre, correo, telefono, contrasena);
    }

    /**
     * HU-01: registra a la administradora en la plataforma.
     * Lleva @Override porque es la versión del Administrador del método abstracto de Usuario.
     */
    @Override
    public void registrarse() {
        registrarCorreo();
        System.out.println("Administrador registrado: " + getNombre() + " (" + getCorreo() + ")");
    }

    // ---------- Permisos de los barberos (HU-25) ----------

    /**
     * HU-25, criterio 1: asigna o modifica los permisos de un barbero asociado.
     * Reemplaza la lista de permisos del barbero por la nueva. Para modificarlos se vuelve a llamar
     * con la lista actualizada.
     * Se guarda una copia de la lista, asi los permisos quedan vinculados al perfil de ese barbero
     * y no se comparten con otro barbero ni con la lista original.
     */
    public void asignarPermisos(Barbero barbero, List<String> permisos) {
        if (barbero == null) {
            throw new IllegalArgumentException("No se pueden asignar permisos: falta el barbero");
        }
        if (permisos == null) {
            throw new IllegalArgumentException("No se pueden asignar permisos: falta la lista de permisos");
        }

        // Se valida todo antes de cambiar nada: si hay un permiso vacio, el barbero queda como estaba
        List<String> nuevos = new ArrayList<>();
        for (String permiso : permisos) {
            if (permiso == null || permiso.trim().isEmpty()) {
                throw new IllegalArgumentException("No se pueden asignar permisos: hay un permiso vacio");
            }
            String limpio = permiso.trim();
            if (!nuevos.contains(limpio)) {
                nuevos.add(limpio);
            }
        }

        barbero.setPermisos(nuevos);
        System.out.println("Permisos de " + barbero.getNombre() + " actualizados: " + nuevos);
    }

    // ---------- HU-28: Cancelar cualquier cita ----------

    /**
     * HU-28: la administradora cancela cualquier cita de cualquier barbero.
     * No tiene la regla de las 2 horas del cliente (HU-14): puede cancelar en cualquier momento.
     * Reutiliza Cita.cancelar(), que cambia el estado a "Cancelada" y libera el horario.
     */
    public void cancelarCita(Cita cita) {
        // La cita debe existir
        if (cita == null) {
            throw new IllegalArgumentException("No se puede cancelar: falta la cita");
        }
        // Criterio 1: se cancela sin revisar cuanto falta para la cita
        // Criterio 2: Cita.cancelar() libera el horario en la agenda del barbero
        cita.cancelar();
    }

    // ---------- HU-27: Reprogramar cualquier cita ----------

    /**
     * HU-27: la administradora reprograma cualquier cita de cualquier barbero a un nuevo horario.
     * No tiene la regla de las 2 horas del cliente (HU-15): puede reprogramar en cualquier momento,
     * y tampoco valida que la cita sea propia.
     * Reutiliza Cita.reprogramar(), que rechaza las citas canceladas y los horarios que el barbero
     * no tiene disponibles. Criterio 2: asi nunca quedan dos citas del mismo barbero en el mismo horario.
     */
    public void reprogramarCita(Cita cita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        // La cita y el nuevo horario deben existir
        if (cita == null) {
            throw new IllegalArgumentException("No se puede reprogramar: falta la cita");
        }
        if (nuevaFecha == null || nuevaHora == null) {
            throw new IllegalArgumentException("No se puede reprogramar: falta la nueva fecha o la nueva hora");
        }
        // Criterio 1: se reprograma sin revisar cuanto falta para la cita
        // Criterio 2: Cita.reprogramar() valida que el barbero tenga libre el nuevo horario
        cita.reprogramar(nuevaFecha, nuevaHora);
    }

    // ---------- HU-26: Crear citas para cualquier barbero ----------

    /**
     * HU-26: la administradora crea una cita en la agenda de cualquier barbero para un cliente.
     * Devuelve la cita creada y confirmada.
     * Reutiliza Cliente.agendarCita(), que valida que el barbero tenga libre ese horario
     * (criterio 2: nunca quedan dos citas del mismo barbero en el mismo horario), crea la cita,
     * la guarda en la agenda del barbero y en la lista del cliente, y la confirma.
     * Asi el cliente despues puede cancelar o reprogramar esta cita como cualquier otra.
     */
    public Cita crearCita(Barbero barbero, Cliente cliente, Servicio servicio, LocalDate fecha, LocalTime hora) {
        // Todos los datos de la cita deben existir
        if (barbero == null) {
            throw new IllegalArgumentException("No se puede crear la cita: falta el barbero");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("No se puede crear la cita: falta el cliente");
        }
        if (servicio == null) {
            throw new IllegalArgumentException("No se puede crear la cita: falta el servicio");
        }
        if (fecha == null || hora == null) {
            throw new IllegalArgumentException("No se puede crear la cita: falta la fecha o la hora");
        }
        // Criterio 1: la administradora puede crear la cita en la agenda de cualquier barbero
        // Criterio 2: Cliente.agendarCita() rechaza los horarios que el barbero no tiene disponibles
        return cliente.agendarCita(barbero, servicio, fecha, hora);
    }
    
    // ---------- HU-29: Configurar el horario de atención ----------

    /**
     * HU-29, criterio 1: define o modifica las horas en que la barbería atiende citas.
     * Recibe la lista completa de horas y reemplaza el horario anterior.
     * Quita las horas repetidas y las ordena de menor a mayor.
     * Criterio 2: Barbero.horariosDisponibles() lee este horario, así que a los clientes
     * solo se les ofrecen horas dentro del horario vigente.
     * Las citas ya agendadas no se borran: si quedan por fuera del nuevo horario,
     * la administradora puede reprogramarlas (HU-27) o cancelarlas (HU-28).
     */
    public void configurarHorarioAtencion(List<LocalTime> horas) {
        // La lista debe tener al menos una hora
        if (horas == null || horas.isEmpty()) {
            throw new IllegalArgumentException("No se puede configurar el horario: la lista de horas esta vacia");
        }
        // Se valida todo antes de cambiar nada: si hay una hora vacía, el horario queda como estaba
        List<LocalTime> nuevas = new ArrayList<>();
        for (LocalTime hora : horas) {
            if (hora == null) {
                throw new IllegalArgumentException("No se puede configurar el horario: hay una hora vacia");
            }
            if (!nuevas.contains(hora)) {
                nuevas.add(hora);
            }
        }
        Collections.sort(nuevas);
        Barberia.setHorarioAtencion(nuevas);
    }
}