package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente de la barbería. Hereda de Usuario (extends) los datos personales y el registro.
 * Agrega la edad, el tipo de cliente, los puntos y sus citas.
 * HU que implementa: HU-01 (registro), HU-03 (agendar), HU-14 (cancelar) y HU-15 (reprogramar).
 */
public class Cliente extends Usuario {

    // Reglas del negocio como constantes (static final): se escriben una sola vez y no cambian
    private static final int EDAD_MINIMA = 15;                // HU-01, criterio 2
    private static final int HORAS_MINIMAS_ANTICIPACION = 2;  // HU-14 y HU-15: cancelar o reprogramar hasta 2 h antes

    private int edad;
    private String tipoCliente; // "Frecuente" (registrado, acumula puntos) | "No frecuente" (atendido en el mostrador)
    private int puntos;
    private List<Cita> citas = new ArrayList<>(); // citas agendadas por este cliente (también las canceladas)

    // Constructor vacío
    public Cliente() {
        super();
    }

    // HU-01, criterio 1: crea la cuenta con nombre, correo, teléfono y edad.
    // Sobrecarga del constructor: cliente registrado en la plataforma (Frecuente, empieza con 0 puntos).
    // super(...) envía los datos personales al constructor de Usuario.
    public Cliente(String nombre, String correo, String telefono, String contrasena, int edad) {
        super(nombre, correo, telefono, contrasena);
        validarEdad(edad); // HU-01, criterio 2: si es menor de 15 no se crea la cuenta
        this.edad = edad;
        this.tipoCliente = "Frecuente";
        this.puntos = 0;
    }

    // HU-01, criterio 2: el sistema impide el registro de menores de 15 años.
    // Lanza un error si la edad es menor a EDAD_MINIMA.
    private static void validarEdad(int edad) {
        if (edad < EDAD_MINIMA) {
            throw new IllegalArgumentException(
                "Registro no permitido: la edad minima es de " + EDAD_MINIMA + " anos");
        }
    }

    // ---------- Getters y setters ----------

    public int getEdad() { return edad; }

    // El setter también valida la edad, para que no se pueda saltar la regla de HU-01
    public void setEdad(int edad) {
        validarEdad(edad);
        this.edad = edad;
    }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    /**
     * HU-01: registra al cliente en la plataforma.
     * Lleva @Override porque es la versión del Cliente del método abstracto registrarse() de Usuario.
     * registrarCorreo() aplica el criterio 3 (correo no repetido).
     */
    @Override
    public void registrarse() {
        registrarCorreo();
        System.out.println("Cliente registrado: " + getNombre() + " (" + getCorreo() + ")");
    }

    // ---------- HU-03: Agendar una cita ----------

    /**
     * HU-03: el cliente agenda una cita con un barbero en un horario disponible.
     * Devuelve la cita creada y confirmada.
     */
    public Cita agendarCita(Barbero barbero, Servicio servicio, LocalDate fecha, LocalTime hora) {
        // Criterio 1: solo se puede agendar en un horario disponible del barbero
        if (!barbero.horariosDisponibles(fecha).contains(hora)) {
            throw new IllegalArgumentException(
                "No se puede agendar: " + barbero.getNombre() + " no tiene disponible " + fecha + " a las " + hora);
        }

        // Criterio 2: se crea la cita con fecha, hora, servicio y barbero, y queda confirmada.
        // La misma cita se guarda en la agenda del barbero y en la lista del cliente.
        Cita cita = new Cita(this, barbero, servicio, fecha, hora);
        barbero.agregarCita(cita);
        citas.add(cita);
        cita.confirmar();
        return cita;
    }

    // ---------- HU-14: Cancelar una cita ----------

    /**
     * HU-14: el cliente cancela una de sus citas.
     * La regla de las 2 horas está aquí y no en Cita, porque el barbero (HU-22)
     * y la administradora (HU-28) pueden cancelar en cualquier momento.
     */
    public void cancelarCita(Cita cita) {
        // Solo puede cancelar sus propias citas
        if (!citas.contains(cita)) {
            throw new IllegalArgumentException(
                "No se puede cancelar: la cita #" + cita.getId() + " no pertenece a " + getNombre());
        }
        // Criterio 1: solo se permite cancelar hasta 2 horas antes
        if (cita.horasParaLaCita() < HORAS_MINIMAS_ANTICIPACION) {
            throw new IllegalStateException(
                "No se puede cancelar: faltan menos de " + HORAS_MINIMAS_ANTICIPACION + " horas para la cita");
        }
        // Criterio 2: la cita queda cancelada y el horario se libera
        cita.cancelar();
    }

    // ---------- HU-15: Reprogramar una cita ----------

    /**
     * HU-15: el cliente reprograma una de sus citas a otro horario del mismo barbero.
     * Igual que en HU-14, la regla de las 2 horas solo aplica al cliente.
     */
    public void reprogramarCita(Cita cita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        // Solo puede reprogramar sus propias citas
        if (!citas.contains(cita)) {
            throw new IllegalArgumentException(
                "No se puede reprogramar: la cita #" + cita.getId() + " no pertenece a " + getNombre());
        }
        // Criterio 2: solo se permite reprogramar hasta 2 horas antes
        if (cita.horasParaLaCita() < HORAS_MINIMAS_ANTICIPACION) {
            throw new IllegalStateException(
                "No se puede reprogramar: faltan menos de " + HORAS_MINIMAS_ANTICIPACION + " horas para la cita");
        }
        // Criterio 1: la cita pasa al nuevo horario, si está disponible (lo valida Cita.reprogramar)
        cita.reprogramar(nuevaFecha, nuevaHora);
    }
}
