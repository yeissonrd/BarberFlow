package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Administradora de la barberia. Hereda de Usuario (extends) los datos personales y el registro.
 * Por ahora no tiene atributos propios: sus funciones (gestionar barberos, servicios,
 * horarios y todas las agendas) llegan en los siguientes sprints.
 * HU que implementa: HU-01 (registro) y HU-25 (asignar permisos a los barberos).
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
}
