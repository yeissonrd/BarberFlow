<p align="center">
  <img src="img/logo.png" alt="Logo BarberFlow" width="160">
</p>

<h1 align="center">BarberFlow</h1>

<p align="center">
  Sistema web para la gestión de citas, barberos y clientes de una barbería.<br>
  Proyecto académico de <b>Metodologías Ágiles</b> — CESDE
</p>

---

## 📌 Descripción

**BarberFlow** permite que los clientes de una barbería agenden, cancelen y reprogramen sus citas, consulten sus puntos de fidelización y vean los servicios disponibles. Los barberos gestionan su propia agenda, y la administradora controla barberos, servicios, horarios de atención y el panel financiero del negocio.

El proyecto se desarrolla con **Scrum**: las historias de usuario (HU) se gestionan en **Jira** y cada HU se implementa en su propia rama de Git.

## 👥 Equipo

| Integrante | Rol |
|---|---|
| **Yeisson Fernando Mora Rodríguez** | Product Owner / Developer |
| **Yully Milena Varela Isaza** | Scrum Master / Developer |

## 🧑‍💼 Roles del sistema

| Rol | Qué puede hacer |
|---|---|
| **Cliente** | Registrarse, elegir servicio y barbero, agendar, cancelar (hasta 2 h antes) y reprogramar citas, consultar y canjear puntos. |
| **Barbero** | Consultar su agenda, crear, reprogramar y cancelar citas de su propia agenda, registrar servicios, pagos y ventas de productos. |
| **Administrador** | Gestionar clientes, barberos, servicios y productos; asignar permisos y día de descanso; configurar el horario de atención; gestionar todas las agendas y consultar el panel financiero. |

## 🛠️ Tecnologías

- **Frontend:** HTML5, CSS3 y JavaScript
- **Backend:** Java (programación orientada a objetos)
- **Herramientas:** Visual Studio Code, Git y GitHub, Jira

## 📁 Estructura del proyecto

```
BarberFlow/
├── index.html          # Página principal
├── login.html          # Inicio de sesión
├── reserva.html        # Reserva de citas
├── agenda.html         # Agenda del barbero
├── panel.html          # Panel de administración
├── perfil.html         # Perfil del usuario
├── css/                # Estilos
├── js/                 # Lógica del frontend (data.js, main.js)
├── img/                # Imágenes y logo
└── backend/
    └── src/
        ├── modelo/     # Clases: Usuario, Cliente, Barbero, Administrador,
        │               #         Cita, Servicio, Barberia
        └── PruebasHU/  # Pruebas de cada historia de usuario (PruebaHUxx.java)
```

## ▶️ Cómo ejecutar

**Frontend**
1. Clonar el repositorio.
2. Abrir `index.html` en el navegador (o con la extensión *Live Server* de VS Code).

**Backend (Java)**
1. Tener instalado el **JDK 21** o superior.
2. Abrir la carpeta del proyecto en VS Code con el **Extension Pack for Java**.
3. Abrir cualquier archivo `backend/src/PruebasHU/PruebaHUxx.java` y dar clic en **Run** (encima de `main`).

## 🌿 Flujo de trabajo con Git

| Rama | Uso |
|---|---|
| `main` | Versión estable entregada al final de cada sprint. |
| `develop` | Rama de integración; aquí se unen las HU terminadas. |
| `feature/hu-XX-nombre` | Una rama por historia de usuario. |

1. Crear la rama desde `develop`: `git checkout -b feature/hu-XX-nombre`
2. Hacer commits por criterio de aceptación: `HU-XX criterio N: descripción`
3. Subir la rama y abrir un **Pull Request** hacia `develop`.
4. El otro integrante revisa, aprueba y hace el merge.

## 📋 Avance — Sprint 1

| HU | Historia | Responsable | Estado |
|---|---|---|---|
| HU-01 | Registrarse en la plataforma | Yully | ✅ Listo |
| HU-11 | Gestionar mi perfil | Yully | ✅ Listo |
| HU-03 | Agendar una cita | Yeisson | ✅ Listo |
| HU-14 | Cancelar una cita | Yeisson | ✅ Listo |
| HU-15 | Reprogramar una cita | Yeisson | ✅ Listo |

---

<p align="center">CESDE · Metodologías Ágiles · 2026</p>
