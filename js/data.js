/* =====================================================
   BarberFlow · datos de DEMOSTRACIÓN
   Todo lo de este archivo es de ejemplo (no es real).
   En un sistema real vendría de una base de datos
   a través de un backend (API).
   ===================================================== */
window.BF = window.BF || {};

BF.servicios = [
  { id: "corte",  nombre: "Corte de cabello", icono: "✂", min: 30, precio: 25000, puntos: 10, desc: "Corte clásico o moderno, con acabado a tu gusto." },
  { id: "barba",  nombre: "Barba",            icono: "🧔", min: 25, precio: 18000, puntos: 8,  desc: "Perfilado y arreglo de barba con toalla caliente." },
  { id: "cejas",  nombre: "Cejas",            icono: "✦", min: 15, precio: 8000,  puntos: 5,  desc: "Diseño y perfilado de cejas." },
  { id: "skin",   nombre: "Skin care",        icono: "✚", min: 40, precio: 35000, puntos: 12, desc: "Limpieza facial e hidratación con especialista." }
];

BF.barberos = [
  { id: "julian", nombre: "Julián Gómez",  especialidad: "Fades y cortes modernos" },
  { id: "kevin",  nombre: "Kevin Ramírez", especialidad: "Barba y perfilado" },
  { id: "mateo",  nombre: "Mateo Salazar", especialidad: "Skin care y cejas" }
];

BF.recompensas = [
  { puntos: 50,  nombre: "Cejas gratis" },
  { puntos: 100, nombre: "Corte de cabello gratis" },
  { puntos: 150, nombre: "Corte + barba gratis" }
];

BF.horarios = ["09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00", "17:00"];

BF.cliente = { nombre: "Andrés Torres", correo: "andres@ejemplo.com", telefono: "300 123 4567", puntos: 68 };

BF.historial = [
  { fecha: "12 jun 2026", servicio: "Corte de cabello", barbero: "Julián Gómez", puntos: 10 },
  { fecha: "22 may 2026", servicio: "Barba",            barbero: "Kevin Ramírez", puntos: 8 },
  { fecha: "02 may 2026", servicio: "Skin care",        barbero: "Mateo Salazar", puntos: 12 }
];

/* Datos del panel de administración (demo) */
BF.admin = {
  clientes: [
    { nombre: "Laura Mejía",   correo: "laura@ejemplo.com",  telefono: "310 555 0101", tipo: "Frecuente" },
    { nombre: "Diego Rojas",   correo: "diego@ejemplo.com",  telefono: "311 555 0102", tipo: "Frecuente" },
    { nombre: "Camilo Pérez",  correo: "camilo@ejemplo.com", telefono: "312 555 0103", tipo: "No frecuente" },
    { nombre: "Sofía Gil",     correo: "sofia@ejemplo.com",  telefono: "313 555 0104", tipo: "No frecuente" }
  ],
  barberos: [
    { nombre: "Julián Gómez",  especialidad: "Fades y cortes modernos", estado: "Activo" },
    { nombre: "Kevin Ramírez", especialidad: "Barba y perfilado",       estado: "Activo" },
    { nombre: "Mateo Salazar", especialidad: "Skin care y cejas",       estado: "Activo" }
  ],
  servicios: [
    { nombre: "Corte de cabello", precio: 25000, duracion: "30 min" },
    { nombre: "Barba",            precio: 18000, duracion: "25 min" },
    { nombre: "Cejas",            precio: 8000,  duracion: "15 min" },
    { nombre: "Skin care",        precio: 35000, duracion: "40 min" }
  ],
  productos: [
    { nombre: "Cera para peinar",   precio: 22000, stock: 14 },
    { nombre: "Aceite para barba",  precio: 28000, stock: 9 },
    { nombre: "Shampoo premium",    precio: 30000, stock: 6 }
  ],
  finanzas: {
    actual: {
      etiqueta: "Mes actual",
      ingServicios: 4850000, ingProductos: 1120000,
      gastos: [ ["Alquiler del local", 1500000], ["Insumos", 620000], ["Servicios públicos", 380000], ["Nómina", 1800000] ],
      pagos: { Efectivo: 2900000, Tarjeta: 2100000, Transferencia: 970000 }
    },
    anterior: {
      etiqueta: "Mes anterior",
      ingServicios: 4300000, ingProductos: 980000,
      gastos: [ ["Alquiler del local", 1500000], ["Insumos", 540000], ["Servicios públicos", 350000], ["Nómina", 1800000] ],
      pagos: { Efectivo: 2600000, Tarjeta: 1800000, Transferencia: 880000 }
    }
  }
};
