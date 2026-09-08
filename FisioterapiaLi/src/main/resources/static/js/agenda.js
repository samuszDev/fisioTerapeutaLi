// REGISTRO DE VISTAS INTERNAS PARA EL MODAL DE AGENDA
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        "new-appointment": {
            eyebrow: "NUEVA RESERVA",
            title: "Agendar cita",
            content: `
                <form id="newAppointmentForm">
                    <div class="internal-section">
                        <h3>Datos de la cita</h3>
                        <div class="form-grid">
                            <div class="form-field">
                                <label>Paciente</label>
                                <select name="idUsuario" id="selectPaciente" required>
                                    <option value="">Cargando pacientes...</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label>Servicio</label>
                                <select name="idServicio" id="selectServicio" required>
                                    <option value="">Cargando servicios...</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label>Fecha</label>
                                <input type="date" name="fecha" id="appointmentDate" placeholder="dd/mm/aaaa" required>
                            </div>
                            <div class="form-field">
                                <label>Hora</label>
                                <input type="time" name="hora" placeholder="hh:mm" required>
                            </div>
                            <div class="form-field">
                                <label>Sede</label>
                                <select name="sede" required>
                                    <option value="">Seleccionar...</option>
                                    <option value="Tunja">Sede Tunja</option>
                                    <option value="Turmequé">Sede Turmequé</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label>Estado de pago</label>
                                <select name="estadoPago" required>
                                    <option value="">Seleccionar...</option>
                                    <option value="PENDIENTE">Pendiente</option>
                                    <option value="PAGADO">Pagado</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="internal-section">
                        <h3>Notas</h3>
                        <div class="form-grid">
                            <div class="form-field full">
                                <label>Observaciones</label>
                                <textarea name="observaciones" rows="4" placeholder="Detalles adicionales o notas de la cita"></textarea>
                            </div>
                        </div>
                    </div>
                </form>
            `
        },

        "edit-appointment": {
            eyebrow: "EDICIÓN DE RESERVA",
            title: "Modificar cita",
            content: `
                <form id="editAppointmentForm">
                    <!-- ESTE INPUT OCULTO ES VITAL PARA ENVIAR EL ID AL SERVIDOR -->
                    <input type="hidden" name="idReserva" id="editIdReserva">

                    <div class="internal-section">
                        <h3>Cita seleccionada</h3>
                        <div class="form-grid">
                            <div class="form-field">
                                <label>Paciente</label>
                                <input type="text" name="nombrePaciente" id="editPacienteNombre" readonly>
                            </div>
                            <div class="form-field">
                                <label>Servicio</label>
                                <input type="text" name="nombreServicio" id="editServicioNombre" readonly>
                            </div>
                        </div>
                    </div>

                    <div class="internal-section">
                        <h3>Actualizar detalles</h3>
                        <div class="form-grid">
                            <div class="form-field">
                                <label>Sede</label>
                                <select name="sede" required>
                                    <option value="Tunja">Sede Tunja</option>
                                    <option value="Turmequé">Sede Turmequé</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label>Nueva fecha</label>
                                <input type="date" name="fecha" placeholder="dd/mm/aaaa" required>
                            </div>
                            <div class="form-field">
                                <label>Nueva hora</label>
                                <input type="time" name="hora" placeholder="hh:mm" required>
                            </div>
                            <div class="form-field">
                                <label>Estado</label>
                                <select name="estado" required>
                                    <option value="PENDIENTE">Pendiente</option>
                                    <option value="CONFIRMADA">Confirmada</option>
                                    <option value="CANCELADA">Cancelada</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label>Pago</label>
                                <select name="estadoPago" required>
                                    <option value="PENDIENTE">Pendiente</option>
                                    <option value="PAGADO">Pagado</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="internal-section">
                        <h3>Motivo del cambio</h3>
                        <div class="form-grid">
                            <div class="form-field full">
                                <label>Motivo del cambio</label>
                                <textarea name="motivoCambio" rows="3" placeholder="Razón del cambio de horario o estado"></textarea>
                            </div>
                        </div>
                    </div>
                </form>
            `
        }
    });
}

// VARIABLE GLOBAL PARA ALMACENAR LAS CITAS
let todasLasCitas = [];

// CARGA INICIAL DESDE EL BACKEND
document.addEventListener("DOMContentLoaded", () => {
    fetch("/api/modal/citas")
        .then(response => {
            if (!response.ok) throw new Error("Error del servidor: " + response.status);
            return response.json();
        })
        .then(citas => {
            todasLasCitas = citas;
            actualizarTarjetasAgenda(todasLasCitas);
            aplicarFiltros();
        })
        .catch(error => {
            console.error("Error al obtener la lista de citas:", error);
            const tableBody = document.getElementById("agendaRows");
            if (tableBody) tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>Error al cargar las citas. Verifica que el servidor esté encendido.</td></tr>";
        });

    // EVENTO DEL BOTÓN FILTRAR
    const btnFiltrar = document.getElementById("btnFiltrar");
    if (btnFiltrar) {
        btnFiltrar.addEventListener("click", (e) => {
            e.preventDefault();
            aplicarFiltros();
        });
    }

    // Filtrar al presionar Enter en la búsqueda
    const searchInput = document.getElementById("filtroPaciente");
    if (searchInput) {
        searchInput.addEventListener("keyup", (e) => {
            if (e.key === "Enter") aplicarFiltros();
        });
    }

    // Filtrar automáticamente al cambiar los selectores
    const selectsFiltro = document.querySelectorAll(".filter-select");
    selectsFiltro.forEach(select => select.addEventListener("change", aplicarFiltros));
});

// LÓGICA DE FILTRADO FLEXIBLE Y ROBUSTA
function aplicarFiltros() {
    const textoPaciente = document.getElementById("filtroPaciente")?.value || "";
    const valEstado = document.getElementById("filtroEstado")?.value || "";
    const valSede = document.getElementById("filtroSede")?.value || "";

    const normalizeText = (str) => {
        if (!str) return "";
        return str.toString().normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().trim();
    };

    const searchNormal = normalizeText(textoPaciente);
    const estadoSelectNormal = normalizeText(valEstado);
    const sedeSelectNormal = normalizeText(valSede);

    const citasFiltradas = todasLasCitas.filter(cita => {
        const nombrePaciente = cita.usuario ? normalizeText(cita.usuario.nombre) : "";
        const estadoCita = normalizeText(cita.estadoCita || "confirmada");
        const sedeCita = normalizeText(cita.sede || "tunja");

        const cumplePaciente = searchNormal === "" || nombrePaciente.includes(searchNormal);
        const cumpleEstado = estadoSelectNormal === "" || estadoCita.includes(estadoSelectNormal);
        const cumpleSede = sedeSelectNormal === "" || sedeCita.includes(sedeSelectNormal);

        return cumplePaciente && cumpleEstado && cumpleSede;
    });

    renderizarTablaCitas(citasFiltradas);
    cargarVistaSemanal(citasFiltradas);
}

// RENDERIZAR TABLA PRINCIPAL (Lista)
function renderizarTablaCitas(citasArray) {
    const tableBody = document.getElementById("agendaRows");
    if (!tableBody) return;

    if (!Array.isArray(citasArray) || citasArray.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>No se encontraron citas con estos filtros.</td></tr>";
        return;
    }

    tableBody.innerHTML = "";

    citasArray.forEach(cita => {
        let fechaHora = 'Sin definir';
        if (cita.fechaInicio) {
            fechaHora = cita.fechaInicio.replace('T', ' ').substring(0, 16);
        }

        const pacienteNombre = cita.usuario ? cita.usuario.nombre : 'N/A';
        const servicioNombre = cita.servicio ? (cita.servicio.nombreS || cita.servicio.nombre || cita.servicio.nombreService) : 'N/A';
        const textoPago = cita.pagoConfirmado ? 'Pagado' : 'Pendiente';
        const estadoCita = cita.estadoCita || 'Confirmada';
        const idCita = cita.idReserva || cita.id;
        const fechaYMD = cita.fechaInicio ? cita.fechaInicio.substring(0, 10) : '';
        const horaHM = cita.fechaInicio ? cita.fechaInicio.substring(11, 16) : '';

        let estadoClass = "confirmed";
        if (estadoCita.toUpperCase() === "PENDIENTE") estadoClass = "pending";
        if (estadoCita.toUpperCase() === "CANCELADA") estadoClass = "cancelled";

        tableBody.innerHTML += `
            <tr>
                <td>${fechaHora}</td>
                <td>${pacienteNombre}</td>
                <td>${servicioNombre}</td>
                <td>${cita.sede || 'Tunja'}</td>
                <td>${textoPago}</td>
                <td><span class="status ${estadoClass}">${estadoCita}</span></td>
                <td>
                    <button class="table-action btn-edit-cita" data-open-view="edit-appointment" 
                        data-id="${idCita}"
                        data-paciente="${pacienteNombre}"
                        data-servicio="${servicioNombre}"
                        data-sede="${cita.sede || 'Tunja'}"
                        data-fecha="${fechaYMD}"
                        data-hora="${horaHM}"
                        data-estado="${cita.estadoCita || 'CONFIRMADA'}"
                        data-pago="${cita.pagoConfirmado ? 'PAGADO' : 'PENDIENTE'}">Editar</button>
                    <button class="table-action btn-delete-cita" data-id="${idCita}" style="color: #d9534f; margin-left: 6px;">Eliminar</button>
                </td>
            </tr>
        `;
    });
}

// ACTUALIZA LOS NÚMEROS DE LAS TARJETAS SUPERIORES
function actualizarTarjetasAgenda(citas) {
    const elHoy = document.getElementById("statCitasHoy");
    const elConfirmadas = document.getElementById("statConfirmadas");
    const elPendientes = document.getElementById("statPendientes");
    const elCanceladas = document.getElementById("statCanceladas");

    if (!elHoy || !Array.isArray(citas)) return;

    const hoyStr = new Date().toISOString().substring(0, 10);

    const citasHoy = citas.filter(c => c.fechaInicio && c.fechaInicio.startsWith(hoyStr)).length;
    const confirmadas = citas.filter(c => c.estadoCita && c.estadoCita.toUpperCase() === "CONFIRMADA").length;
    const pendientes = citas.filter(c => c.estadoCita && c.estadoCita.toUpperCase() === "PENDIENTE").length;
    const canceladas = citas.filter(c => c.estadoCita && c.estadoCita.toUpperCase() === "CANCELADA").length;

    elHoy.textContent = citasHoy;
    if (elConfirmadas) elConfirmadas.textContent = confirmadas;
    if (elPendientes) elPendientes.textContent = pendientes;
    if (elCanceladas) elCanceladas.textContent = canceladas;
}

// RENDERIZA LAS TARJETAS EN LA VISTA SEMANAL
function cargarVistaSemanal(citasArray) {
    const contenedoresDias = {
        1: document.getElementById('col-lunes'),
        2: document.getElementById('col-martes'),
        3: document.getElementById('col-miercoles'),
        4: document.getElementById('col-jueves'),
        5: document.getElementById('col-viernes'),
        6: document.getElementById('col-sabado'),
        0: document.getElementById('col-domingo')
    };

    Object.values(contenedoresDias).forEach(contenedor => {
        if (contenedor) contenedor.innerHTML = '';
    });

    if (!Array.isArray(citasArray) || citasArray.length === 0) return;

    const citasOrdenadas = [...citasArray].sort((a, b) => {
        if (!a.fechaInicio) return 1;
        if (!b.fechaInicio) return -1;
        return a.fechaInicio.localeCompare(b.fechaInicio);
    });

    const hoy = new Date();
    const diaSemanaIndex = hoy.getDay() === 0 ? 7 : hoy.getDay();

    const inicioSemana = new Date(hoy);
    inicioSemana.setDate(hoy.getDate() - diaSemanaIndex + 1);
    inicioSemana.setHours(0, 0, 0, 0);

    const finSemana = new Date(inicioSemana);
    finSemana.setDate(inicioSemana.getDate() + 6);
    finSemana.setHours(23, 59, 59, 999);

    citasOrdenadas.forEach(cita => {
        if (!cita.fechaInicio) return;

        const partes = cita.fechaInicio.split("T");
        if (partes.length < 2) return;

        const [anio, mes, dia] = partes[0].split("-").map(Number);
        const [hora, min] = partes[1].split(":").map(Number);

        const fechaCita = new Date(anio, mes - 1, dia, hora, min);

        if (fechaCita >= inicioSemana && fechaCita <= finSemana) {
            const numeroDia = fechaCita.getDay();
            const contenedor = contenedoresDias[numeroDia];

            if (contenedor) {
                const pacienteNombre = cita.usuario ? cita.usuario.nombre : 'Paciente';
                const horaFormateada = `${hora.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')}`;
                const servicioNombre = cita.servicio ? (cita.servicio.nombreS || cita.servicio.nombre || 'Servicio') : '';
                const estadoCita = (cita.estadoCita || "Confirmada").toUpperCase();

                let borderColor = "#d4af37";
                if (estadoCita === "PENDIENTE") borderColor = "#6c757d";
                if (estadoCita === "CANCELADA") borderColor = "#d9534f";

                contenedor.innerHTML += `
                    <div style="background-color: #ffffff; border-left: 4px solid ${borderColor}; padding: 6px 8px; margin-bottom: 8px; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); font-size: 0.75rem;">
                        <strong style="color: #0b2239; display: block; font-size: 0.8rem;">${horaFormateada}</strong>
                        <div style="color: #212529; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${pacienteNombre}</div>
                        <div style="color: #6c757d; font-size: 0.7rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">${servicioNombre}</div>
                    </div>
                `;
            }
        }
    });
}

// LISTENER GLOBAL PARA EDITAR CITAS
document.addEventListener("click", event => {
    if (event.target.classList.contains("btn-edit-cita")) {
        const btn = event.target;
        setTimeout(() => {
            const idInput = document.getElementById("editIdReserva");
            if (idInput) idInput.value = btn.dataset.id;

            const pacienteInput = document.getElementById("editPacienteNombre");
            if (pacienteInput) pacienteInput.value = btn.dataset.paciente;

            const servicioInput = document.getElementById("editServicioNombre");
            if (servicioInput) servicioInput.value = btn.dataset.servicio;

            const sedeSelect = document.querySelector("#editAppointmentForm [name='sede']");
            if (sedeSelect) sedeSelect.value = btn.dataset.sede;

            const fechaInput = document.querySelector("#editAppointmentForm [name='fecha']");
            if (fechaInput) fechaInput.value = btn.dataset.fecha;

            const horaInput = document.querySelector("#editAppointmentForm [name='hora']");
            if (horaInput) horaInput.value = btn.dataset.hora;

            const estadoSelect = document.querySelector("#editAppointmentForm [name='estado']");
            if (estadoSelect) estadoSelect.value = btn.dataset.estado;

            const pagoSelect = document.querySelector("#editAppointmentForm [name='estadoPago']");
            if (pagoSelect) pagoSelect.value = btn.dataset.pago;
        }, 50);
    }
});

// LISTENER GLOBAL PARA ELIMINAR CITAS
document.addEventListener("click", event => {
    if (event.target.classList.contains("btn-delete-cita")) {
        const idCita = event.target.dataset.id;

        if (confirm("¿Estás seguro de que deseas eliminar esta cita de la agenda?")) {
            fetch(`/api/modal/citas/${idCita}`, {
                method: "DELETE"
            })
                .then(async response => {
                    if (response.ok) {
                        alert("¡Cita eliminada correctamente!");
                        window.location.reload();
                    } else {
                        const errorMsg = await response.text();
                        alert("Error al eliminar: " + errorMsg);
                    }
                })
                .catch(error => {
                    console.error("Error de red al eliminar cita:", error);
                    alert("Error al conectar con el servidor.");
                });
        }
    }
});