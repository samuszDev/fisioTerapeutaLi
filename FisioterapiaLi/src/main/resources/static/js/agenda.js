// REGISTRO DE VISTAS INTERNAS PARA EL MODAL DE AGENDA
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        "new-appointment": {
            eyebrow: "NUEVA RESERVA",
            title: "Agendar cita",
            content: `
                <form class="form-grid" id="newAppointmentForm">
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
                    <div class="form-field full">
                        <label>Observaciones</label>
                        <textarea name="observaciones" rows="4" placeholder="Detalles adicionales o notas de la cita"></textarea>
                    </div>
                </form>
            `
        },

        "edit-appointment": {
            eyebrow: "EDICIÓN DE RESERVA",
            title: "Modificar cita",
            content: `
                <form class="form-grid" id="editAppointmentForm">
                    <div class="form-field">
                        <label>Paciente</label>
                        <input type="text" name="nombrePaciente" id="editPacienteNombre" readonly>
                    </div>
                    <div class="form-field">
                        <label>Servicio</label>
                        <input type="text" name="nombreServicio" id="editServicioNombre" readonly>
                    </div>
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
                    <div class="form-field full">
                        <label>Motivo del cambio</label>
                        <textarea name="motivoCambio" rows="3" placeholder="Razón del cambio de horario o estado"></textarea>
                    </div>
                </form>
            `
        }
    });
}

// CARGA DINÁMICA DE CITAS DESDE LA BASE DE DATOS
document.addEventListener("DOMContentLoaded", () => {
    const tableBody = document.getElementById("agendaRows");

    if (tableBody) {
        fetch("/api/modal/citas")
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error del servidor: " + response.status);
                }
                return response.json();
            })
            .then(citas => {
                if (!Array.isArray(citas) || citas.length === 0) {
                    tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>No hay citas agendadas.</td></tr>";
                    return;
                }

                tableBody.innerHTML = ""; // Limpiar tabla

                citas.forEach(cita => {
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

                    tableBody.innerHTML += `
                        <tr>
                            <td>${fechaHora}</td>
                            <td>${pacienteNombre}</td>
                            <td>${servicioNombre}</td>
                            <td>${cita.sede || 'Tunja'}</td>
                            <td>${textoPago}</td>
                            <td><span class="status">${estadoCita}</span></td>
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
            })
            .catch(error => {
                console.error("Error al obtener la lista de citas:", error);
                tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>Error al cargar las citas. Verifica que el servidor esté encendido.</td></tr>";
            });
    }
});

// Listener global para el botón de editar cita (cargar datos en el formulario)
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

// Listener global para eliminar citas desde la tabla
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