package modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase padre abstracta de los tres roles de BarberFlow: Cliente, Barbero y Administrador.
 * Reúne los datos y acciones que comparten todos (HU-01 registro y HU-11 perfil).
 * Es abstracta porque nadie es "solo usuario": siempre se crea un Cliente, un Barbero o un Administrador.
 */
public abstract class Usuario {

    // Contador compartido por todos los usuarios para asignar un id único y consecutivo
    private static int contador = 1;

    // Atributos privados (encapsulamiento): solo se acceden con getters y setters
    private int id;
    private String nombre;
    private String correo;
    private String telefono;
    private String contrasena;          // sin getter: solo se comprueba con verificarContrasena()
    private String enlaceRecuperacion;  // HU-11, criterio 2: enlace vigente de recuperación (null si no hay)

    // Constructor vacío: solo asigna el id
    public Usuario() {
        this.id = contador++;
    }

    // Sobrecarga del constructor: crea el usuario con sus datos personales
    // this() llama al constructor vacío para que también reciba su id
    public Usuario(String nombre, String correo, String telefono, String contrasena) {
        this();
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }

    // ---------- Getters y setters ----------

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    /**
     * HU-01: registra la cuenta en la plataforma.
     * Es abstracto: cada rol (Cliente, Barbero, Administrador) escribe su propia versión con @Override.
     */
    public abstract void registrarse();

    // ---------- HU-11: Gestionar mi perfil ----------

    /**
     * HU-11, criterio 1: el sistema permite editar los datos del perfil en cualquier momento.
     * Si un dato llega en null, ese dato no se modifica.
     * Primero se valida todo y después se aplican los cambios, para no dejar el perfil a medias.
     */
    public void editarPerfil(String nuevoNombre, String nuevoCorreo, String nuevoTelefono) {
        // Validación 1: el nombre no puede quedar vacío
        if (nuevoNombre != null && nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Edicion no permitida: el nombre no puede estar vacio");
        }

        // Validación 2: el correo no puede quedar vacío ni pertenecer a otra cuenta
        // Se compara en minúsculas y sin espacios para que "ANDRES@x.com" y "andres@x.com" sean el mismo
        String correoNormalizado = null;
        if (nuevoCorreo != null) {
            if (nuevoCorreo.trim().isEmpty()) {
                throw new IllegalArgumentException("Edicion no permitida: el correo no puede estar vacio");
            }
            correoNormalizado = nuevoCorreo.trim().toLowerCase();
            String anterior = (correo == null) ? "" : correo.trim().toLowerCase();
            if (!correoNormalizado.equals(anterior) && correosRegistrados.contains(correoNormalizado)) {
                throw new IllegalArgumentException("Edicion no permitida: el correo ya esta registrado");
            }
        }

        // Todo validado: se aplican los cambios
        if (nuevoNombre != null) {
            this.nombre = nuevoNombre.trim();
        }
        if (nuevoCorreo != null) {
            String anterior = (correo == null) ? "" : correo.trim().toLowerCase();
            if (!correoNormalizado.equals(anterior)) {
                // Si la cuenta ya estaba registrada, se cambia el correo en la lista:
                // el anterior queda libre para otra cuenta y el nuevo queda ocupado
                if (correosRegistrados.remove(anterior)) {
                    correosRegistrados.add(correoNormalizado);
                }
            }
            this.correo = nuevoCorreo.trim();
        }
        if (nuevoTelefono != null) {
            this.telefono = nuevoTelefono.trim();
        }
    }

    /**
     * HU-11, criterio 2: el sistema permite recuperar la contraseña mediante un enlace
     * enviado al correo registrado.
     * Genera un enlace único con UUID. El envío del correo se simula imprimiendo el enlace en consola.
     */
    public void recuperarContrasena() {
        // Solo una cuenta registrada puede recuperar su contraseña
        if (correo == null || !correosRegistrados.contains(correo.trim().toLowerCase())) {
            throw new IllegalStateException("Recuperacion no permitida: el correo no esta registrado");
        }
        enlaceRecuperacion = "https://barberflow.com/recuperar?token=" + UUID.randomUUID();
        System.out.println("Enlace de recuperacion enviado a " + correo + ": " + enlaceRecuperacion);
    }

    // HU-11, criterio 2: permite leer el enlace generado (simula abrir el correo)
    public String getEnlaceRecuperacion() { return enlaceRecuperacion; }

    /**
     * HU-11, criterio 2: simula que la persona abre el enlace y escribe su nueva contraseña.
     * El enlace debe ser el que se generó y solo sirve una vez (después queda en null).
     */
    public void restablecerContrasena(String enlace, String nuevaContrasena) {
        if (enlaceRecuperacion == null || !enlaceRecuperacion.equals(enlace)) {
            throw new IllegalArgumentException("Recuperacion no permitida: el enlace no es valido");
        }
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("Recuperacion no permitida: la contrasena no puede estar vacia");
        }
        this.contrasena = nuevaContrasena;
        this.enlaceRecuperacion = null; // el enlace ya no se puede volver a usar
    }

    // HU-11: comprueba si una contraseña es correcta sin exponerla con un getter (encapsulamiento)
    public boolean verificarContrasena(String intento) {
        return contrasena != null && contrasena.equals(intento);
    }

    // ---------- HU-01: Registrarse en la plataforma ----------

    // HU-01, criterio 3: lista de correos ya registrados en el sistema.
    // Es static porque es una sola lista para todos los roles: un correo no se puede repetir
    // aunque sea de un cliente, un barbero o la administradora.
    private static final List<String> correosRegistrados = new ArrayList<>();

    /**
     * HU-01, criterio 3: el sistema valida que el correo no esté registrado antes de crear la cuenta.
     * Si está libre, lo guarda en la lista. Se llama desde registrarse() de cada rol.
     * Es protected (#): solo lo usan Usuario y sus clases hijas.
     */
    protected void registrarCorreo() {
        String correoActual = getCorreo();
        if (correoActual == null || correoActual.trim().isEmpty()) {
            throw new IllegalArgumentException("Registro no permitido: el correo es obligatorio");
        }
        String normalizado = correoActual.trim().toLowerCase();
        if (correosRegistrados.contains(normalizado)) {
            throw new IllegalArgumentException("Registro no permitido: el correo ya esta registrado");
        }
        correosRegistrados.add(normalizado);
    }
}
