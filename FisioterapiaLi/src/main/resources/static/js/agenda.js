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
                                <input type="date" name="fecha" id="appointmentDate" required>
                            </div>
                            <div class="form-field">
                                <label>Hora</label>
                                <input type="time" name="hora" required>
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
                </form>
            `
        },

        "edit-appointment": {
            eyebrow: "EDICIÓN DE RESERVA",
            title: "Modificar cita",
            content: `
                <form id="editAppointmentForm">
                    <input type="hidden" name="idReserva" id="editIdReserva">
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
                                <input type="date" name="fecha" required>
                            </div>
                            <div class="form-field">
                                <label>Nueva hora</label>
                                <input type="time" name="hora" required>
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
                </form>
            `
        }
    });
}

let todasLasCitas = [];

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
        .catch(error => console.error("Error al obtener citas:", error));

    const btnFiltrar = document.getElementById("btnFiltrar");
    if (btnFiltrar) {
        btnFiltrar.addEventListener("click", (e) => {
            e.preventDefault();
            aplicarFiltros();
        });
    }
});

function aplicarFiltros() {
    const textoPaciente = document.getElementById("filtroPaciente")?.value || "";
    const valEstado = document.getElementById("filtroEstado")?.value || "";
    const valSede = document.getElementById("filtroSede")?.value || "";

    const normalizeText = (str) => str ? str.toString().normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().trim() : "";

    const citasFiltradas = todasLasCitas.filter(cita => {
        const nombrePaciente = cita.usuario ? normalizeText(cita.usuario.nombre) : "";
        const estadoCita = normalizeText(cita.estadoCita || "confirmada");
        const sedeCita = normalizeText(cita.sede || "tunja");

        return (textoPaciente === "" || nombrePaciente.includes(normalizeText(textoPaciente))) &&
            (valEstado === "" || estadoCita.includes(normalizeText(valEstado))) &&
            (valSede === "" || sedeCita.includes(normalizeText(valSede)));
    });

    renderizarTablaCitas(citasFiltradas);
    cargarVistaSemanal(citasFiltradas);
}

function renderizarTablaCitas(citasArray) {
    const tableBody = document.getElementById("agendaRows");
    if (!tableBody) return;

    if (!Array.isArray(citasArray) || citasArray.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>No se encontraron citas.</td></tr>";
        return;
    }

    tableBody.innerHTML = "";
    citasArray.forEach(cita => {
        const fechaHora = cita.fechaInicio ? cita.fechaInicio.replace('T', ' ').substring(0, 16) : 'Sin definir';
        const pacienteNombre = cita.usuario ? cita.usuario.nombre : 'N/A';
        const servicioNombre = cita.servicio ? (cita.servicio.nombreS || cita.servicio.nombre) : 'N/A';
        const idCita = cita.idReserva || cita.id;

        tableBody.innerHTML += `
            <tr>
                <td>${fechaHora}</td>
                <td>${pacienteNombre}</td>
                <td>${servicioNombre}</td>
                <td>${cita.sede || 'Tunja'}</td>
                <td>${cita.pagoConfirmado ? 'Pagado' : 'Pendiente'}</td>
                <td><span class="status">${cita.estadoCita || 'CONFIRMADA'}</span></td>
                <td>
                    <button class="table-action btn-edit-cita" data-open-view="edit-appointment" data-id="${idCita}">Editar</button>
                    <button class="table-action btn-delete-cita" data-id="${idCita}" style="color: #d9534f; margin-left: 6px;">Eliminar</button>
                </td>
            </tr>
        `;
    });
}

function actualizarTarjetasAgenda(citas) {
    const elHoy = document.getElementById("statCitasHoy");
    if (!elHoy || !Array.isArray(citas)) return;
    const hoyStr = new Date().toISOString().substring(0, 10);
    elHoy.textContent = citas.filter(c => c.fechaInicio && c.fechaInicio.startsWith(hoyStr)).length;
}

function cargarVistaSemanal(citasArray) {
    // Lógica de renderizado semanal
}

// CARGAR SELECTS DE PACIENTES Y SERVICIOS AL ABRIR EL MODAL DE NUEVA CITA
document.addEventListener("click", event => {
    const trigger = event.target.closest('[data-open-view="new-appointment"]');
    if (trigger) {
        setTimeout(() => {
            const selectPaciente = document.getElementById("selectPaciente");
            const selectServicio = document.getElementById("selectServicio");

            if (selectPaciente) {
                fetch("/api/modal/pacientes")
                    .then(res => res.json())
                    .then(pacientes => {
                        selectPaciente.innerHTML = '<option value="">Selecciona un paciente...</option>';
                        pacientes.forEach(p => {
                            selectPaciente.innerHTML += `<option value="${p.idUsuario}">${p.nombre} (Doc: ${p.documento})</option>`;
                        });
                    });
            }

            if (selectServicio) {
                fetch("/api/modal/servicios")
                    .then(res => res.json())
                    .then(servicios => {
                        selectServicio.innerHTML = '<option value="">Selecciona un servicio...</option>';
                        servicios.forEach(s => {
                            if (s.estadoS !== false) {
                                const idServ = s.idServicio || s.id;
                                const nombreS = s.nombreS || s.nombreService || 'Servicio';
                                selectServicio.innerHTML += `<option value="${idServ}">${nombreS}</option>`;
                            }
                        });
                    });
            }
        }, 50);
    }
});

// ELIMINAR CITA
document.addEventListener("click", event => {
    if (event.target.classList.contains("btn-delete-cita")) {
        const idCita = event.target.dataset.id;
        if (confirm("¿Estás seguro de eliminar esta cita?")) {
            fetch(`/api/modal/citas/${idCita}`, { method: "DELETE" })
                .then(res => {
                    if (res.ok) {
                        alert("Cita eliminada correctamente");
                        window.location.reload();
                    } else {
                        res.text().then(msg => alert("Error: " + msg));
                    }
                });
        }
    }
});
function cargarVistaSemanal(citasArray) {
    // 1. Definir los contenedores de los días de la semana
    const diasContenedores = {
        0: document.getElementById('col-domingo'),
        1: document.getElementById('col-lunes'),
        2: document.getElementById('col-martes'),
        3: document.getElementById('col-miercoles'),
        4: document.getElementById('col-jueves'),
        5: document.getElementById('col-viernes'),
        6: document.getElementById('col-sabado')
    };

    // 2. Limpiar todas las columnas antes de pintar
    Object.values(diasContenedores).forEach(contenedor => {
        if (contenedor) contenedor.innerHTML = '';
    });

    if (!Array.isArray(citasArray) || citasArray.length === 0) return;

    // 3. Recorrer y distribuir las citas según su fecha de inicio
    citasArray.forEach(cita => {
        if (!cita.fechaInicio) return;

        // Extraer la fecha (ej: "2026-09-08T10:00:00" -> se toma la parte de la fecha)
        const fechaStr = cita.fechaInicio.split('T')[0];
        const partesFecha = fechaStr.split('-');

        if (partesFecha.length !== 3) return;

        // Crear la fecha asegurando zona horaria local o interpretando año, mes (0-11), día
        const anio = parseInt(partesFecha[0], 10);
        const mes = parseInt(partesFecha[1], 10) - 1;
        const dia = parseInt(partesFecha[2], 10);
        const fechaObj = new Date(anio, mes, dia);

        const diaSemana = fechaObj.getDay(); // 0 (Dom) al 6 (Sáb)
        const contenedorDia = diasContenedores[diaSemana];

        if (contenedorDia) {
            const horaStr = cita.fechaInicio.includes('T') ? cita.fechaInicio.split('T')[1].substring(0, 5) : '';
            const pacienteNombre = cita.usuario ? cita.usuario.nombre : 'Paciente';
            const servicioNombre = cita.servicio ? (cita.servicio.nombreS || cita.servicio.nombre) : '';
            const estadoCita = cita.estadoCita || 'CONFIRMADA';

            const card = document.createElement('div');
            card.className = `weekly-appointment-card estado-${estadoCita.toLowerCase()}`;
            card.style.cssText = "background: #fff; border-left: 4px solid var(--gold, #c5a880); padding: 8px; margin-bottom: 6px; border-radius: 4px; box-shadow: 0 1px 3px rgba(0,0,0,0.05); font-size: 13px;";

            card.innerHTML = `
                <div style="font-weight: 600; color: #1e293b;">${horaStr} - ${pacienteNombre}</div>
                <div style="color: #64748b; font-size: 12px;">${servicioNombre}</div>
            `;

            contenedorDia.appendChild(card);
        }
    });
}