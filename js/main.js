/* =====================================================
   BarberFlow · comportamiento (JavaScript sencillo)
   Cada página activa su propio módulo según
   <body data-page="...">.
   Sin backend: las citas se guardan en localStorage
   solo para la demostración.
   ===================================================== */
(function () {
  "use strict";

  /* ---------- Utilidades ---------- */
  const $ = (sel, root = document) => root.querySelector(sel);
  const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));
  const money = (n) => "$" + Number(n).toLocaleString("es-CO");
  const esc = (s) => String(s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
  const pad = (n) => String(n).padStart(2, "0");
  const toISO = (d) => `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
  const byId = (list, id) => list.find((x) => x.id === id);
  const MESES = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"];
  const DIAS = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"];

  function fmtFecha(iso) {
    const [y, m, d] = iso.split("-").map(Number);
    return `${d} de ${MESES[m - 1]} de ${y}`;
  }

  /* Generador pseudoaleatorio con semilla (datos de ejemplo estables) */
  function seeded(str) {
    let h = 1779033703 ^ str.length;
    for (let i = 0; i < str.length; i++) { h = Math.imul(h ^ str.charCodeAt(i), 3432918353); h = (h << 13) | (h >>> 19); }
    let a = h >>> 0;
    return function () {
      a |= 0; a = (a + 0x6d2b79f5) | 0;
      let t = Math.imul(a ^ (a >>> 15), 1 | a);
      t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
      return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
    };
  }

  /* ---------- Almacenamiento de citas (demo) ---------- */
  const KEY_CITAS = "bf_citas";
  const KEY_PUNTOS = "bf_puntos";

  function citasPorDefecto() {
    const hoy = new Date();
    const d1 = new Date(hoy); d1.setDate(hoy.getDate() + 3);
    const d2 = new Date(hoy); d2.setDate(hoy.getDate() + 10);
    return [
      { id: "c1", servicio: "corte", barbero: "julian", fecha: toISO(d1), hora: "10:00", estado: "Confirmada" },
      { id: "c2", servicio: "skin", barbero: "mateo", fecha: toISO(d2), hora: "15:00", estado: "Confirmada" }
    ];
  }
  function leerCitas() {
    try {
      const raw = localStorage.getItem(KEY_CITAS);
      if (raw) return JSON.parse(raw).filter((c) => byId(BF.barberos, c.barbero) && byId(BF.servicios, c.servicio));
    } catch (e) { /* sin almacenamiento disponible */ }
    return citasPorDefecto();
  }
  function guardarCitas(lista) {
    try { localStorage.setItem(KEY_CITAS, JSON.stringify(lista)); } catch (e) { /* ignorar */ }
  }
  function leerPuntos() {
    try { const v = localStorage.getItem(KEY_PUNTOS); if (v !== null) return Number(v); } catch (e) { /* ignorar */ }
    return BF.cliente.puntos;
  }
  function guardarPuntos(n) {
    try { localStorage.setItem(KEY_PUNTOS, String(n)); } catch (e) { /* ignorar */ }
  }

  function inicioCita(c) {
    const [y, m, d] = c.fecha.split("-").map(Number);
    const [h, mi] = c.hora.split(":").map(Number);
    return new Date(y, m - 1, d, h, mi);
  }

  /* Mensaje accesible (aria-live) */
  function anunciar(texto, tipo) {
    const box = $("#live");
    if (!box) return;
    box.className = "alert alert--" + (tipo || "info");
    box.textContent = texto;
    box.hidden = false;
  }

  /* =====================================================
     Componentes reutilizables: cabecera, pie, diálogo
     ===================================================== */
  function renderHeader() {
    const host = $("#site-header");
    if (!host) return;
    const page = document.body.dataset.page;
    const cur = (name) => (page === name ? ' aria-current="page"' : "");
    host.className = "site-header";
    host.innerHTML = `
      <div class="container site-header__inner">
        <a class="brand" href="index.html" aria-label="BarberFlow, ir al inicio">
          <img src="img/logo.png" alt="Logo de BarberFlow"> <span>BarberFlow</span>
        </a>
        <button class="nav-toggle" aria-expanded="false" aria-controls="nav" aria-label="Abrir menú">☰</button>
        <nav class="nav" id="nav" aria-label="Principal">
          <ul>
            <li><a href="index.html"${cur("inicio")}>Inicio</a></li>
            <li><a href="index.html#servicios">Servicios</a></li>
            <li><a href="index.html#barberos">Barberos</a></li>
            <li><a href="agenda.html"${cur("agenda")}>Agenda</a></li>
            <li><a class="btn btn--outline" href="login.html"${cur("login")}>Iniciar sesión</a></li>
          </ul>
        </nav>
      </div>`;
    const btn = $(".nav-toggle", host);
    const nav = $("#nav", host);
    btn.addEventListener("click", () => {
      const abierto = nav.classList.toggle("is-open");
      btn.setAttribute("aria-expanded", String(abierto));
      btn.textContent = abierto ? "✕" : "☰";
    });
    $$("a", nav).forEach((a) => a.addEventListener("click", () => {
      nav.classList.remove("is-open"); btn.setAttribute("aria-expanded", "false"); btn.textContent = "☰";
    }));
  }

  function renderFooter() {
    const host = $("#site-footer");
    if (!host) return;
    host.className = "site-footer";
    host.innerHTML = `
      <div class="container">
        <div class="footer__grid">
          <div>
            <a class="brand" href="index.html"><img src="img/logo.png" alt="Logo de BarberFlow"> <span>BarberFlow</span></a>
            <p class="muted small" style="margin-top:.8rem">Reserva fácil, barbería organizada.</p>
          </div>
          <div>
            <h3>Contacto</h3>
            <ul>
              <li><a href="mailto:contacto@barberflow.example">contacto@barberflow.example</a></li>
              <li><a href="tel:+570000000000">+57 000 000 0000</a></li>
              <li class="muted small">Dirección y teléfono de ejemplo</li>
            </ul>
          </div>
          <div>
            <h3>Cuenta</h3>
            <ul>
              <li><a href="reserva.html">Agendar una cita</a></li>
              <li><a href="perfil.html">Mi perfil</a></li>
              <li><a href="panel.html">Administración</a></li>
              <li><button type="button" class="link-dark" id="btn-sugerencias">Sugerencias</button></li>
            </ul>
          </div>
          <div>
            <h3>Redes sociales</h3>
            <ul>
              <li><a href="https://www.instagram.com/" target="_blank" rel="noopener">Instagram</a></li>
              <li><a href="https://www.facebook.com/" target="_blank" rel="noopener">Facebook</a></li>
              <li><a href="https://www.tiktok.com/" target="_blank" rel="noopener">TikTok</a></li>
            </ul>
          </div>
        </div>
        <p class="footer__bottom">© ${new Date().getFullYear()} BarberFlow · Prototipo de demostración</p>
      </div>
      <dialog id="dlg-sugerencias" aria-labelledby="sug-title">
        <form method="dialog" class="form" id="form-sugerencias" novalidate>
          <h2 id="sug-title">Sugerencias</h2>
          <p class="muted">Cuéntanos cómo mejorar BarberFlow.</p>
          <div class="field"><label for="sug-texto">Tu sugerencia</label><textarea id="sug-texto" rows="4" required></textarea>
            <span class="field__msg" id="sug-msg" role="alert"></span></div>
          <div class="dialog-actions">
            <button class="btn btn--outline btn--sm" value="cancel" formnovalidate>Cerrar</button>
            <button class="btn btn--sm" id="sug-enviar" type="button">Enviar</button>
          </div>
        </form>
      </dialog>`;
    const dlg = $("#dlg-sugerencias");
    $("#btn-sugerencias").addEventListener("click", () => dlg.showModal());
    $("#sug-enviar").addEventListener("click", () => {
      const t = $("#sug-texto"), msg = $("#sug-msg");
      if (t.value.trim().length < 5) { msg.textContent = "Escribe al menos 5 caracteres."; t.focus(); return; }
      msg.style.color = "#2f7d4f"; msg.textContent = "¡Gracias! Tu sugerencia fue recibida (demo).";
      t.value = "";
      setTimeout(() => { dlg.close(); msg.textContent = ""; msg.style.color = ""; }, 1400);
    });
  }

  /* Lista de citas con acciones (usada en Reserva y Perfil) */
  function renderCitas(host, opciones) {
    const citas = leerCitas().filter((c) => c.estado !== "Cancelada" && inicioCita(c) >= new Date(Date.now() - 3600000))
      .sort((a, b) => inicioCita(a) - inicioCita(b));
    if (!citas.length) { host.innerHTML = '<p class="muted">No tienes citas próximas.</p>'; return; }
    host.innerHTML = citas.map((c) => {
      const s = byId(BF.servicios, c.servicio), b = byId(BF.barberos, c.barbero);
      return `<div class="appt" data-id="${esc(c.id)}">
        <div><strong>${esc(s.nombre)}</strong> con ${esc(b.nombre)}<br>
          <span class="muted">${fmtFecha(c.fecha)} · ${esc(c.hora)}</span>
          <span class="badge badge--ok">${esc(c.estado)}</span></div>
        <div class="appt__actions">
          <a class="btn btn--outline btn--sm" href="reserva.html?reprogramar=${encodeURIComponent(c.id)}">Reprogramar</a>
          <button class="btn btn--danger btn--sm" data-cancelar="${esc(c.id)}">Cancelar</button>
        </div></div>`;
    }).join("");
    $$("[data-cancelar]", host).forEach((btn) => btn.addEventListener("click", () => {
      const lista = leerCitas();
      const c = lista.find((x) => x.id === btn.dataset.cancelar);
      if (!c) return;
      const horas = (inicioCita(c) - new Date()) / 3600000;
      if (horas < 2) { anunciar("Solo puedes cancelar hasta 2 horas antes de la cita.", "err"); return; }
      if (!window.confirm("¿Seguro que quieres cancelar esta cita?")) return;
      c.estado = "Cancelada";
      guardarCitas(lista);
      renderCitas(host, opciones);
      if (opciones && opciones.onChange) opciones.onChange();
      anunciar("Cita cancelada. El horario quedó libre.", "ok");
    }));
  }

  /* =====================================================
     INICIO
     ===================================================== */
  function pageInicio() {
    $("#lista-servicios").innerHTML = BF.servicios.map((s) => `
      <article class="card">
        <div class="card__icon" aria-hidden="true">${s.icono}</div>
        <h3>${esc(s.nombre)}</h3>
        <p class="muted">${esc(s.desc)}</p>
        <p><span class="price">${money(s.precio)}</span> · ${s.min} min</p>
        <a class="btn btn--outline btn--sm" href="reserva.html?servicio=${s.id}">Reservar</a>
      </article>`).join("");
    $("#lista-barberos").innerHTML = BF.barberos.map((b) => `
      <article class="card barber-card">
        <div class="avatar" aria-hidden="true">${esc(b.nombre[0])}</div>
        <h3>${esc(b.nombre)}</h3>
        <p><span class="tag">${esc(b.especialidad)}</span></p>
        <a class="btn btn--outline btn--sm" href="agenda.html?barbero=${b.id}">Ver agenda</a>
      </article>`).join("");
  }

  /* =====================================================
     LOGIN / REGISTRO
     ===================================================== */
  function pageLogin() {
    const tabs = $$(".tab"), panels = $$(".tab-panel");
    function mostrar(id) {
      tabs.forEach((t) => t.setAttribute("aria-selected", String(t.dataset.tab === id)));
      panels.forEach((p) => (p.hidden = p.id !== "panel-" + id));
    }
    tabs.forEach((t) => t.addEventListener("click", () => { mostrar(t.dataset.tab); history.replaceState(null, "", "#" + t.dataset.tab); }));
    mostrar(location.hash === "#registro" ? "registro" : "login");
    $$("[data-goto]").forEach((b) => b.addEventListener("click", () => mostrar(b.dataset.goto)));

    /* Mostrar / ocultar contraseña */
    $$(".pass-toggle").forEach((btn) => btn.addEventListener("click", () => {
      const input = $("#" + btn.dataset.target);
      const visible = input.type === "text";
      input.type = visible ? "password" : "text";
      btn.textContent = visible ? "Mostrar" : "Ocultar";
      btn.setAttribute("aria-pressed", String(!visible));
    }));

    const setError = (input, msg) => {
      const f = input.closest(".field");
      f.classList.toggle("field--error", !!msg);
      $(".field__msg", f).textContent = msg || "";
      input.setAttribute("aria-invalid", msg ? "true" : "false");
      return !msg;
    };
    const emailOk = (v) => /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(v);

    /* Inicio de sesión */
    $("#form-login").addEventListener("submit", (e) => {
      e.preventDefault();
      const f = e.target, ok = [];
      ok.push(setError(f.elements.correo, emailOk(f.elements.correo.value.trim()) ? "" : "Ingresa un correo válido."));
      ok.push(setError(f.elements.clave, f.elements.clave.value.length >= 8 ? "" : "La contraseña debe tener al menos 8 caracteres."));
      const res = $("#login-result");
      if (ok.includes(false)) { res.className = "alert alert--err"; res.textContent = "Revisa los campos marcados."; res.hidden = false; return; }
      res.className = "alert alert--ok";
      res.innerHTML = 'Sesión iniciada (demostración). <a href="perfil.html"><strong>Ir a mi perfil</strong></a>';
      res.hidden = false;
    });

    /* Registro */
    $("#form-registro").addEventListener("submit", (e) => {
      e.preventDefault();
      const f = e.target, ok = [];
      ok.push(setError(f.elements.nombre, f.elements.nombre.value.trim().length >= 3 ? "" : "Escribe tu nombre completo."));
      ok.push(setError(f.elements.correo, emailOk(f.elements.correo.value.trim()) ? "" : "Ingresa un correo válido."));
      ok.push(setError(f.elements.telefono, /^[0-9 ]{7,13}$/.test(f.elements.telefono.value.trim()) ? "" : "Ingresa un teléfono válido (solo números)."));
      ok.push(setError(f.elements.clave, f.elements.clave.value.length >= 8 ? "" : "La contraseña debe tener al menos 8 caracteres."));
      const edad = Number(f.elements.edad.value);
      let msgEdad = "";
      if (!f.elements.edad.value) msgEdad = "Ingresa tu edad.";
      else if (edad < 15) msgEdad = "Las personas menores de 15 años no pueden registrarse.";
      else if (edad > 100) msgEdad = "Ingresa una edad válida.";
      ok.push(setError(f.elements.edad, msgEdad));
      const res = $("#registro-result");
      if (ok.includes(false)) { res.className = "alert alert--err"; res.textContent = "No pudimos crear la cuenta. Corrige los campos marcados."; res.hidden = false; return; }
      res.className = "alert alert--ok";
      res.innerHTML = "¡Cuenta creada (demostración)! Ya puedes <button type='button' class='link-btn' data-goto-login>iniciar sesión</button>.";
      res.hidden = false;
      $("[data-goto-login]", res).addEventListener("click", () => mostrar("login"));
      f.reset();
    });

    /* Recuperar contraseña */
    const dlg = $("#dlg-recuperar");
    $("#btn-recuperar").addEventListener("click", () => dlg.showModal());
    $("#rec-enviar").addEventListener("click", () => {
      const inp = $("#rec-correo"), msg = $("#rec-msg");
      if (!emailOk(inp.value.trim())) { msg.style.color = ""; msg.textContent = "Ingresa un correo válido."; inp.focus(); return; }
      msg.style.color = "#2f7d4f";
      msg.textContent = "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña (demo).";
    });
  }

  /* =====================================================
     RESERVA DE CITAS
     ===================================================== */
  function pageReserva() {
    const params = new URLSearchParams(location.search);
    const estado = { paso: 1, servicio: null, barbero: null, fecha: "", hora: "", editId: null };

    const cont = (id) => $("#" + id);
    cont("opc-servicios").innerHTML = BF.servicios.map((s) => `
      <label class="choice"><input type="radio" name="servicio" value="${s.id}">
        <span><strong>${esc(s.nombre)}</strong><span class="muted">${s.min} min · ${money(s.precio)}</span></span></label>`).join("");
    cont("opc-barberos").innerHTML = BF.barberos.map((b) => `
      <label class="choice"><input type="radio" name="barbero" value="${b.id}">
        <span><strong>${esc(b.nombre)}</strong><span class="muted">${esc(b.especialidad)}</span></span></label>`).join("");

    const inputFecha = cont("fecha");
    const hoy = new Date();
    inputFecha.min = toISO(hoy);

    /* Precarga: ?servicio=, ?barbero= o ?reprogramar= */
    if (params.get("servicio") && byId(BF.servicios, params.get("servicio"))) estado.servicio = params.get("servicio");
    if (params.get("barbero") && byId(BF.barberos, params.get("barbero"))) estado.barbero = params.get("barbero");
    const rid = params.get("reprogramar");
    if (rid) {
      const c = leerCitas().find((x) => x.id === rid);
      if (c) { estado.editId = c.id; estado.servicio = c.servicio; estado.barbero = c.barbero; estado.paso = 3; }
    }
    if (estado.servicio) $(`input[name=servicio][value=${estado.servicio}]`).checked = true;
    if (estado.barbero) $(`input[name=barbero][value=${estado.barbero}]`).checked = true;
    if (estado.editId) anunciar("Estás reprogramando tu cita: elige una nueva fecha y hora.", "info");

    function horasLibres() {
      const out = [];
      if (!estado.fecha || !estado.barbero) return out;
      const [y, m, d] = estado.fecha.split("-").map(Number);
      const dia = new Date(y, m - 1, d);
      const domingo = dia.getDay() === 0;
      const rnd = seeded(estado.barbero + estado.fecha);
      const ocupadas = leerCitas().filter((c) => c.estado !== "Cancelada" && c.id !== estado.editId && c.barbero === estado.barbero && c.fecha === estado.fecha).map((c) => c.hora);
      BF.horarios.forEach((h) => {
        const [hh] = h.split(":").map(Number);
        const pasada = estado.fecha === toISO(hoy) && hh <= hoy.getHours();
        const tomada = rnd() < 0.3 || ocupadas.includes(h);
        out.push({ hora: h, libre: !domingo && !pasada && !tomada });
      });
      return out;
    }

    function renderHoras() {
      const host = cont("slots");
      const info = cont("slots-info");
      if (!estado.fecha) { host.innerHTML = ""; info.textContent = "Elige una fecha para ver los horarios."; return; }
      const [y, m, d] = estado.fecha.split("-").map(Number);
      if (new Date(y, m - 1, d).getDay() === 0) { host.innerHTML = ""; info.textContent = "Los domingos la barbería está cerrada. Elige otro día."; return; }
      const lista = horasLibres();
      host.innerHTML = lista.map((h) => `<label class="slot"><input type="radio" name="hora" value="${h.hora}" ${h.libre ? "" : "disabled"} ${estado.hora === h.hora && h.libre ? "checked" : ""}><span>${h.hora}</span></label>`).join("");
      const libres = lista.filter((h) => h.libre).length;
      info.textContent = libres ? `${libres} horarios disponibles.` : "No hay horarios disponibles ese día. Prueba con otra fecha.";
      if (!lista.some((h) => h.libre && h.hora === estado.hora)) estado.hora = "";
    }

    function resumen() {
      const s = byId(BF.servicios, estado.servicio), b = byId(BF.barberos, estado.barbero);
      cont("resumen").innerHTML = `
        <div><dt>Servicio</dt><dd>${esc(s.nombre)}</dd></div>
        <div><dt>Barbero</dt><dd>${esc(b.nombre)}</dd></div>
        <div><dt>Fecha</dt><dd>${fmtFecha(estado.fecha)}</dd></div>
        <div><dt>Hora</dt><dd>${esc(estado.hora)}</dd></div>
        <div><dt>Duración</dt><dd>${s.min} min</dd></div>
        <div><dt>Precio</dt><dd>${money(s.precio)}</dd></div>
        <div><dt>Puntos que ganarás</dt><dd>+${s.puntos} pts</dd></div>`;
    }

    function validarPaso() {
      const err = cont("paso-error");
      let msg = "";
      if (estado.paso === 1 && !estado.servicio) msg = "Elige un servicio para continuar.";
      if (estado.paso === 2 && !estado.barbero) msg = "Elige un barbero para continuar.";
      if (estado.paso === 3) {
        if (!estado.fecha) msg = "Selecciona una fecha.";
        else if (!estado.hora) msg = "Selecciona un horario disponible.";
      }
      err.textContent = msg;
      err.hidden = !msg;
      return !msg;
    }

    function mostrarPaso() {
      $$(".wizard-step").forEach((el) => (el.hidden = Number(el.dataset.step) !== estado.paso));
      $$(".stepper li").forEach((li) => {
        const n = Number(li.dataset.step);
        li.classList.toggle("is-active", n === estado.paso);
        li.classList.toggle("is-done", n < estado.paso);
        if (n === estado.paso) li.setAttribute("aria-current", "step"); else li.removeAttribute("aria-current");
      });
      cont("btn-atras").hidden = estado.paso === 1;
      cont("btn-sig").hidden = estado.paso === 4;
      cont("btn-confirmar").hidden = estado.paso !== 4;
      cont("paso-error").hidden = true;
      if (estado.paso === 3) { inputFecha.value = estado.fecha; renderHoras(); }
      if (estado.paso === 4) resumen();
    }

    document.addEventListener("change", (e) => {
      const t = e.target;
      if (t.name === "servicio") estado.servicio = t.value;
      if (t.name === "barbero") { estado.barbero = t.value; estado.hora = ""; }
      if (t.name === "hora") estado.hora = t.value;
      if (t.id === "fecha") { estado.fecha = t.value; estado.hora = ""; renderHoras(); }
    });
    cont("btn-sig").addEventListener("click", () => { if (validarPaso()) { estado.paso++; mostrarPaso(); } });
    cont("btn-atras").addEventListener("click", () => { estado.paso--; mostrarPaso(); });

    cont("btn-confirmar").addEventListener("click", () => {
      const lista = leerCitas();
      if (estado.editId) {
        const c = lista.find((x) => x.id === estado.editId);
        Object.assign(c, { servicio: estado.servicio, barbero: estado.barbero, fecha: estado.fecha, hora: estado.hora, estado: "Confirmada" });
      } else {
        lista.push({ id: "c" + Date.now(), servicio: estado.servicio, barbero: estado.barbero, fecha: estado.fecha, hora: estado.hora, estado: "Confirmada" });
      }
      guardarCitas(lista);
      const s = byId(BF.servicios, estado.servicio), b = byId(BF.barberos, estado.barbero);
      $("#wizard").hidden = true;
      const ok = cont("confirmacion");
      ok.hidden = false;
      ok.innerHTML = `<h2>${estado.editId ? "¡Cita reprogramada!" : "¡Cita confirmada!"}</h2>
        <p>${esc(s.nombre)} con <strong>${esc(b.nombre)}</strong> el <strong>${fmtFecha(estado.fecha)}</strong> a las <strong>${esc(estado.hora)}</strong>.</p>
        <p class="alert alert--info">🔔 Te enviaremos un recordatorio antes de tu cita (demostración).</p>
        <a class="btn" href="reserva.html">Agendar otra cita</a>`;
      ok.focus();
      renderCitas(cont("mis-citas"));
    });

    renderCitas(cont("mis-citas"));
    if (estado.fecha) inputFecha.value = estado.fecha;
    mostrarPaso();
  }

  /* =====================================================
     PERFIL
     ===================================================== */
  function pagePerfil() {
    const c = BF.cliente;
    $("#perfil-datos").innerHTML = `
      <div><dt>Nombre</dt><dd>${esc(c.nombre)}</dd></div>
      <div><dt>Correo</dt><dd>${esc(c.correo)}</dd></div>
      <div><dt>Teléfono</dt><dd>${esc(c.telefono)}</dd></div>`;
    $("#hola").textContent = "Hola, " + c.nombre.split(" ")[0];

    renderCitas($("#perfil-citas"));

    $("#perfil-historial").innerHTML = BF.historial.map((h) => `
      <tr><td>${esc(h.fecha)}</td><td>${esc(h.servicio)}</td><td>${esc(h.barbero)}</td><td class="num">+${h.puntos} pts</td></tr>`).join("");

    $("#puntos-servicio").innerHTML = BF.servicios.map((s) => `<tr><td>${esc(s.nombre)}</td><td class="num">+${s.puntos} pts</td></tr>`).join("");

    function pintarPuntos() {
      const p = leerPuntos();
      $("#puntos-total").textContent = p;
      const siguiente = BF.recompensas.find((r) => r.puntos > p);
      const meta = siguiente ? siguiente.puntos : BF.recompensas[BF.recompensas.length - 1].puntos;
      $("#puntos-fill").style.width = Math.min(100, (p / meta) * 100) + "%";
      $("#puntos-texto").textContent = siguiente ? `Te faltan ${siguiente.puntos - p} puntos para: ${siguiente.nombre}.` : "¡Ya puedes canjear cualquier recompensa!";
      $("#recompensas").innerHTML = BF.recompensas.map((r, i) => `
        <tr><td>${esc(r.nombre)}</td><td class="num">${r.puntos} pts</td>
        <td class="num"><button class="btn btn--sm ${p >= r.puntos ? "" : "btn--outline"}" data-canje="${i}" ${p >= r.puntos ? "" : "disabled"}>Canjear</button></td></tr>`).join("");
      $$("[data-canje]").forEach((b) => b.addEventListener("click", () => {
        const r = BF.recompensas[Number(b.dataset.canje)];
        if (!window.confirm(`¿Canjear ${r.puntos} puntos por "${r.nombre}"?`)) return;
        guardarPuntos(leerPuntos() - r.puntos);
        pintarPuntos();
        anunciar(`¡Canje realizado! Disfruta: ${r.nombre} (demostración).`, "ok");
      }));
    }
    pintarPuntos();
  }

  /* =====================================================
     AGENDA DEL BARBERO (calendario mensual)
     ===================================================== */
  function pageAgenda() {
    const CLIENTES = ["Laura M.", "Diego R.", "Camilo P.", "Sofía G.", "Nico T.", "Ana L.", "Leo S.", "Sol V.", "Andrés T.", "Juan C."];
    const hoy = new Date();
    const params = new URLSearchParams(location.search);
    const sel = { barbero: byId(BF.barberos, params.get("barbero")) ? params.get("barbero") : BF.barberos[0].id, mes: hoy.getMonth(), anio: hoy.getFullYear(), dia: null };

    const selBarbero = $("#sel-barbero"), selMes = $("#sel-mes"), selAnio = $("#sel-anio");
    selBarbero.innerHTML = BF.barberos.map((b) => `<option value="${b.id}">${esc(b.nombre)}</option>`).join("");
    selMes.innerHTML = MESES.map((m, i) => `<option value="${i}">${m[0].toUpperCase() + m.slice(1)}</option>`).join("");
    const anios = []; for (let a = hoy.getFullYear() - 1; a <= hoy.getFullYear() + 2; a++) anios.push(a);
    selAnio.innerHTML = anios.map((a) => `<option value="${a}">${a}</option>`).join("");

    /* Citas de ejemplo para un barbero y mes + las citas reales del cliente demo */
    function citasDelMes() {
      const mapa = {};
      const diasMes = new Date(sel.anio, sel.mes + 1, 0).getDate();
      for (let d = 1; d <= diasMes; d++) {
        const fecha = new Date(sel.anio, sel.mes, d);
        if (fecha.getDay() === 0) continue;
        const rnd = seeded(`${sel.barbero}-${sel.anio}-${sel.mes}-${d}`);
        const n = Math.floor(rnd() * 5);
        const horas = BF.horarios.slice().sort(() => rnd() - 0.5).slice(0, n).sort();
        mapa[d] = horas.map((h) => ({ hora: h, cliente: CLIENTES[Math.floor(rnd() * CLIENTES.length)], servicio: BF.servicios[Math.floor(rnd() * BF.servicios.length)].nombre }));
      }
      leerCitas().filter((c) => c.estado !== "Cancelada" && c.barbero === sel.barbero).forEach((c) => {
        const [y, m, d] = c.fecha.split("-").map(Number);
        if (y === sel.anio && m - 1 === sel.mes) {
          mapa[d] = (mapa[d] || []).filter((e) => e.hora !== c.hora);
          mapa[d].push({ hora: c.hora, cliente: BF.cliente.nombre.split(" ")[0] + " T.", servicio: byId(BF.servicios, c.servicio).nombre });
          mapa[d].sort((a, b) => a.hora.localeCompare(b.hora));
        }
      });
      return mapa;
    }

    function pintar() {
      selBarbero.value = sel.barbero; selMes.value = sel.mes; selAnio.value = sel.anio;
      const barbero = byId(BF.barberos, sel.barbero);
      const citas = citasDelMes();
      $("#cal-titulo").textContent = `${barbero.nombre} · ${MESES[sel.mes]} ${sel.anio}`;
      const primero = new Date(sel.anio, sel.mes, 1);
      const offset = (primero.getDay() + 6) % 7; // semana inicia en lunes
      const diasMes = new Date(sel.anio, sel.mes + 1, 0).getDate();
      let html = DIAS.map((d) => `<div class="cal__dow">${d}</div>`).join("");
      for (let i = 0; i < offset; i++) html += '<div class="cal__day cal__day--empty" aria-hidden="true"></div>';
      let total = 0;
      for (let d = 1; d <= diasMes; d++) {
        const evs = citas[d] || [];
        total += evs.length;
        const esHoy = d === hoy.getDate() && sel.mes === hoy.getMonth() && sel.anio === hoy.getFullYear();
        const cls = "cal__day" + (esHoy ? " cal__day--today" : "") + (sel.dia === d ? " cal__day--selected" : "");
        html += `<button type="button" class="${cls}" data-dia="${d}" aria-label="${d} de ${MESES[sel.mes]}: ${evs.length} citas">
          <span class="cal__num">${d}</span>
          ${evs.slice(0, 2).map((e) => `<span class="cal__ev">${e.hora} ${esc(e.cliente)} · ${esc(e.servicio)}</span>`).join("")}
          ${evs.length > 2 ? `<span class="cal__more">+${evs.length - 2} más</span>` : ""}
          ${evs.length ? `<span class="cal__count">${evs.length}</span>` : ""}
        </button>`;
      }
      $("#cal").innerHTML = html;
      $$("#cal [data-dia]").forEach((b) => b.addEventListener("click", () => { sel.dia = Number(b.dataset.dia); pintar(); }));
      $("#cal-resumen").textContent = `${total} citas en ${MESES[sel.mes]} ${sel.anio} para ${barbero.nombre}.`;

      /* Detalle del día */
      const det = $("#dia-detalle");
      if (!sel.dia) { det.innerHTML = '<p class="muted">Selecciona un día del calendario para ver sus citas.</p>'; return; }
      const evs = citas[sel.dia] || [];
      det.innerHTML = `<h3>${sel.dia} de ${MESES[sel.mes]} de ${sel.anio}</h3>` + (evs.length
        ? evs.map((e) => `<div class="appt"><div><strong>${e.hora}</strong> · ${esc(e.cliente)}<br><span class="muted">${esc(e.servicio)}</span></div></div>`).join("")
        : "<p class='muted'>Sin citas este día (o la barbería está cerrada).</p>");
    }

    selBarbero.addEventListener("change", () => { sel.barbero = selBarbero.value; sel.dia = null; pintar(); });
    selMes.addEventListener("change", () => { sel.mes = Number(selMes.value); sel.dia = null; pintar(); });
    selAnio.addEventListener("change", () => { sel.anio = Number(selAnio.value); sel.dia = null; pintar(); });
    function mover(delta) {
      const f = new Date(sel.anio, sel.mes + delta, 1);
      if (f.getFullYear() < anios[0] || f.getFullYear() > anios[anios.length - 1]) return;
      sel.mes = f.getMonth(); sel.anio = f.getFullYear(); sel.dia = null; pintar();
    }
    $("#btn-prev").addEventListener("click", () => mover(-1));
    $("#btn-next").addEventListener("click", () => mover(1));
    $("#btn-hoy").addEventListener("click", () => { sel.mes = hoy.getMonth(); sel.anio = hoy.getFullYear(); sel.dia = hoy.getDate(); pintar(); });
    pintar();
  }

  /* =====================================================
     ADMINISTRACIÓN Y PANEL FINANCIERO
     ===================================================== */
  function pageAdmin() {
    /* Pestañas */
    const tabs = $$(".tab"), panels = $$(".tab-panel");
    function mostrar(id) {
      tabs.forEach((t) => t.setAttribute("aria-selected", String(t.dataset.tab === id)));
      panels.forEach((p) => (p.hidden = p.id !== "panel-" + id));
    }
    tabs.forEach((t) => t.addEventListener("click", () => mostrar(t.dataset.tab)));
    tabs.forEach((t, i) => t.addEventListener("keydown", (e) => {
      if (e.key === "ArrowRight" || e.key === "ArrowLeft") {
        const n = tabs[(i + (e.key === "ArrowRight" ? 1 : tabs.length - 1)) % tabs.length];
        n.focus(); mostrar(n.dataset.tab);
      }
    }));

    /* Finanzas */
    function finanzas() {
      const f = BF.admin.finanzas[$("#fin-periodo").value];
      const ing = f.ingServicios + f.ingProductos;
      const gas = f.gastos.reduce((a, g) => a + g[1], 0);
      const bal = ing - gas;
      $("#kpis").innerHTML = `
        <div class="kpi"><span>Ingresos totales</span><strong>${money(ing)}</strong></div>
        <div class="kpi kpi--neg"><span>Gastos</span><strong>${money(gas)}</strong></div>
        <div class="kpi ${bal >= 0 ? "kpi--pos" : "kpi--neg"}"><span>Balance</span><strong>${money(bal)}</strong></div>`;
      const barra = (label, val, tot, extra) => `<div class="bar-row"><div class="bar-row__label"><span>${esc(label)}</span><strong>${money(val)} · ${Math.round((val / tot) * 100)}%</strong></div><div class="bar ${extra || ""}"><i style="width:${(val / tot) * 100}%"></i></div></div>`;
      $("#fin-ingresos").innerHTML = barra("Servicios", f.ingServicios, ing) + barra("Productos", f.ingProductos, ing);
      $("#fin-gastos").innerHTML = f.gastos.map((g) => barra(g[0], g[1], gas, "bar--red")).join("");
      $("#fin-pagos").innerHTML = Object.entries(f.pagos).map(([k, v]) => barra(k, v, ing)).join("");
    }
    $("#fin-periodo").addEventListener("change", finanzas);
    finanzas();

    /* CRUD genérico (en memoria, solo demostración) */
    const config = {
      clientes:  { cols: [["nombre", "Nombre"], ["correo", "Correo"], ["telefono", "Teléfono"], ["tipo", "Tipo"]], select: { tipo: ["Frecuente", "No frecuente"] } },
      barberos:  { cols: [["nombre", "Nombre"], ["especialidad", "Especialidad"], ["estado", "Estado"]], select: { estado: ["Activo", "Inactivo"] } },
      servicios: { cols: [["nombre", "Nombre"], ["precio", "Precio"], ["duracion", "Duración"]], money: ["precio"] },
      productos: { cols: [["nombre", "Nombre"], ["precio", "Precio"], ["stock", "Stock"]], money: ["precio"], number: ["precio", "stock"] }
    };
    Object.keys(config).forEach((ent) => {
      const cfg = config[ent], data = BF.admin[ent];
      const tbody = $(`#tabla-${ent}`), form = $(`#form-${ent}`);
      let editIdx = null;
      cfg.number = cfg.number || (cfg.money || []);

      form.innerHTML = cfg.cols.map(([k, l]) => {
        const id = `${ent}-${k}`;
        if (cfg.select && cfg.select[k]) return `<div class="field"><label for="${id}">${l}</label><select id="${id}" name="${k}">${cfg.select[k].map((o) => `<option>${o}</option>`).join("")}</select></div>`;
        const tipo = cfg.number.includes(k) ? "number" : "text";
        return `<div class="field"><label for="${id}">${l}</label><input id="${id}" name="${k}" type="${tipo}" ${tipo === "number" ? 'min="0"' : ""} required></div>`;
      }).join("") + `<button class="btn" type="submit">Agregar</button><button class="btn btn--outline" type="button" hidden data-cancelar-edit>Cancelar</button>`;

      function pintar() {
        tbody.innerHTML = data.map((row, i) => `<tr>${cfg.cols.map(([k]) => `<td>${cfg.money && cfg.money.includes(k) ? money(row[k]) : esc(row[k])}</td>`).join("")}
          <td class="num"><button class="btn btn--outline btn--sm" data-edit="${i}">Editar</button> <button class="btn btn--danger btn--sm" data-del="${i}">Eliminar</button></td></tr>`).join("")
          || `<tr><td colspan="${cfg.cols.length + 1}" class="muted">Sin registros.</td></tr>`;
        $$("[data-del]", tbody).forEach((b) => b.addEventListener("click", () => {
          if (!window.confirm("¿Eliminar este registro?")) return;
          data.splice(Number(b.dataset.del), 1); pintar(); salirEdicion();
        }));
        $$("[data-edit]", tbody).forEach((b) => b.addEventListener("click", () => {
          editIdx = Number(b.dataset.edit);
          cfg.cols.forEach(([k]) => (form.elements[k].value = data[editIdx][k]));
          $("button[type=submit]", form).textContent = "Guardar cambios";
          $("[data-cancelar-edit]", form).hidden = false;
          form.elements[cfg.cols[0][0]].focus();
        }));
      }
      function salirEdicion() {
        editIdx = null; form.reset();
        $("button[type=submit]", form).textContent = "Agregar";
        $("[data-cancelar-edit]", form).hidden = true;
      }
      $("[data-cancelar-edit]", form).addEventListener("click", salirEdicion);
      form.addEventListener("submit", (e) => {
        e.preventDefault();
        if (!form.reportValidity()) return;
        const row = {};
        cfg.cols.forEach(([k]) => (row[k] = cfg.number.includes(k) ? Number(form.elements[k].value) : form.elements[k].value.trim()));
        if (editIdx === null) data.push(row); else data[editIdx] = row;
        salirEdicion(); pintar();
      });
      pintar();
    });
  }

  /* ---------- Arranque ---------- */
  document.addEventListener("DOMContentLoaded", () => {
    renderHeader();
    renderFooter();
    const page = document.body.dataset.page;
    const modulos = { inicio: pageInicio, login: pageLogin, reserva: pageReserva, perfil: pagePerfil, agenda: pageAgenda, admin: pageAdmin };
    if (modulos[page]) modulos[page]();
  });
})();
