package modelo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Configuración general del negocio
public class Barberia {

    // Horario de atención por defecto: de 9 a 12 y de 14 a 17.
    // La administradora lo podrá cambiar en HU-29.
    private static List<LocalTime> horarioAtencion = new ArrayList<>(List.of(
        LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(12, 0),
        LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0), LocalTime.of(17, 0)
    ));

    // HU-03: el resto del sistema consulta aquí el horario vigente
    public static List<LocalTime> getHorarioAtencion() {
        return horarioAtencion;
    }
}