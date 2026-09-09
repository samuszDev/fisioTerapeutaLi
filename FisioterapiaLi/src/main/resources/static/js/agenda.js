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
                                <select name="sede" id="editSede" required>
                                    <option value="Tunja">Sede Tunja</option>
                                    <option value="Turmequé">Sede Turmequé</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label>Nueva fecha</label>
                                <input type="date" name="fecha" id="editFecha" required>
                            </div>
                            <div class="form-field">
                                <label>Nueva hora</label>
                                <input type="time" name="hora" id="editHora" required>
                            </div>
                            <div class="form-field">
                                <label>Estado</label>
                                <select name="estado" id="editEstado" required>
                                    <option value="PENDIENTE">Pendiente</option>
                                    <option value="CONFIRMADA">Confirmada</option>
                                    <option value="CANCELADA">Cancelada</option>
                                </select>
                            </div>
                            <div class="form-field">
                                <label>Pago</label>
                                <select name="estadoPago" id="editEstadoPago" required>
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
    cargarCitas();

    const btnFiltrar = document.getElementById("btnFiltrar");
    if (btnFiltrar) {
        btnFiltrar.addEventListener("click", (e) => {
            e.preventDefault();
            aplicarFiltros();
        });
    }

    const btnLimpiar = document.getElementById("btnLimpiar");
    if (btnLimpiar) {
        btnLimpiar.addEventListener("click", (e) => {
            e.preventDefault();
            const inputPaciente = document.getElementById("filtroPaciente");
            const selectEstado = document.getElementById("filtroEstado");
            const selectSede = document.getElementById("filtroSede");

            if (inputPaciente) inputPaciente.value = "";
            if (selectEstado) selectEstado.value = "";
            if (selectSede) selectSede.value = "";

            aplicarFiltros();
        });
    }
});

function cargarCitas() {
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
}

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
    if (!Array.isArray(citas)) return;

    const hoyStr = new Date().toISOString().substring(0, 10);

    // 1. Citas de hoy
    const elHoy = document.getElementById("statCitasHoy");
    if (elHoy) {
        elHoy.textContent = citas.filter(c => c.fechaInicio && c.fechaInicio.startsWith(hoyStr)).length;
    }

    // 2. Confirmadas
    const elConfirmadas = document.getElementById("statConfirmadas");
    if (elConfirmadas) {
        elConfirmadas.textContent = citas.filter(c => {
            const estado = (c.estadoCita || c.estado || "").toUpperCase();
            return estado === "CONFIRMADA";
        }).length;
    }

    // 3. Pendientes
    const elPendientes = document.getElementById("statPendientes");
    if (elPendientes) {
        elPendientes.textContent = citas.filter(c => {
            const estado = (c.estadoCita || c.estado || "").toUpperCase();
            return estado === "PENDIENTE";
        }).length;
    }

    // 4. Canceladas
    const elCanceladas = document.getElementById("statCanceladas");
    if (elCanceladas) {
        elCanceladas.textContent = citas.filter(c => {
            const estado = (c.estadoCita || c.estado || "").toUpperCase();
            return estado === "CANCELADA";
        }).length;
    }
}

function cargarVistaSemanal(citasArray) {
    const diasContenedores = {
        0: document.getElementById('col-domingo'),
        1: document.getElementById('col-lunes'),
        2: document.getElementById('col-martes'),
        3: document.getElementById('col-miercoles'),
        4: document.getElementById('col-jueves'),
        5: document.getElementById('col-viernes'),
        6: document.getElementById('col-sabado')
    };

    Object.values(diasContenedores).forEach(contenedor => {
        if (contenedor) contenedor.innerHTML = '';
    });

    if (!Array.isArray(citasArray) || citasArray.length === 0) return;

    citasArray.forEach(cita => {
        if (!cita.fechaInicio) return;

        const fechaStr = cita.fechaInicio.split('T')[0];
        const partesFecha = fechaStr.split('-');

        if (partesFecha.length !== 3) return;

        const anio = parseInt(partesFecha[0], 10);
        const mes = parseInt(partesFecha[1], 10) - 1;
        const dia = parseInt(partesFecha[2], 10);
        const fechaObj = new Date(anio, mes, dia);

        const diaSemana = fechaObj.getDay();
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

// CARGAR SELECTS DE PACIENTES Y SERVICIOS AL ABRIR EL MODAL DE NUEVA CITA
document.addEventListener("click", event => {
    const triggerNew = event.target.closest('[data-open-view="new-appointment"]');
    if (triggerNew) {
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

    // CARGAR DATOS EN EL MODAL DE EDICIÓN AL HACER CLIC EN "Editar"
    const triggerEdit = event.target.closest('.btn-edit-cita');
    if (triggerEdit) {
        const idCita = triggerEdit.dataset.id;
        const citaEncontrada = todasLasCitas.find(c => (c.idReserva == idCita || c.id == idCita));

        if (citaEncontrada) {
            setTimeout(() => {
                const inputIdReserva = document.getElementById("editIdReserva");
                const selectSede = document.getElementById("editSede");
                const inputFecha = document.getElementById("editFecha");
                const inputHora = document.getElementById("editHora");
                const selectEstado = document.getElementById("editEstado");
                const selectPago = document.getElementById("editEstadoPago");

                if (inputIdReserva) inputIdReserva.value = idCita;
                if (selectSede && citaEncontrada.sede) selectSede.value = citaEncontrada.sede;

                if (citaEncontrada.fechaInicio) {
                    const partes = citaEncontrada.fechaInicio.split('T');
                    if (inputFecha) inputFecha.value = partes[0];
                    if (inputHora && partes[1]) inputHora.value = partes[1].substring(0, 5);
                }

                if (selectEstado && citaEncontrada.estadoCita) {
                    selectEstado.value = citaEncontrada.estadoCita;
                }

                if (selectPago) {
                    selectPago.value = citaEncontrada.pagoConfirmado ? "PAGADO" : "PENDIENTE";
                }
            }, 50);
        }
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
                        // 1. Volvemos a cargar las citas desde el servidor para refrescar el array global
                        cargarCitas();
                    } else {
                        res.text().then(msg => alert("Error: " + msg));
                    }
                });
        }
    }
});