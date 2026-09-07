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
                        <select name="idServicio" id="editSelectServicio" required>
                            <option value="">Cargando servicios...</option>
                        </select>
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
                // ESTO ES CLAVE: Si el backend da error (ej. 405 o 500), no intenta leer JSON
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
                    // 1. Formatear la fechaInicio que viene desde Java (Ej: "2026-09-07T14:30:00")
                    let fechaHora = 'Sin definir';
                    if (cita.fechaInicio) {
                        fechaHora = cita.fechaInicio.replace('T', ' ').substring(0, 16);
                    }

                    // 2. Extraer nombre del paciente
                    const pacienteNombre = cita.usuario ? cita.usuario.nombre : 'N/A';

                    // 3. Mapeo seguro del nombre del servicio (por si cambia en Java)
                    const servicioNombre = cita.servicio ? (cita.servicio.nombre || cita.servicio.nombreService || cita.servicio.nombreServicio) : 'N/A';

                    // 4. Transformar el booleano 'pagoConfirmado' a texto visible
                    const textoPago = cita.pagoConfirmado ? 'Pagado' : 'Pendiente';

                    // 5. Mostrar el estado de la cita
                    const estadoCita = cita.estadoCita || 'Confirmada';

                    tableBody.innerHTML += `
                        <tr>
                            <td>${fechaHora}</td>
                            <td>${pacienteNombre}</td>
                            <td>${servicioNombre}</td>
                            <td>${cita.sede || 'Tunja'}</td>
                            <td>${textoPago}</td>
                            <td><span class="status">${estadoCita}</span></td>
                            <td>
                                <button class="table-action" data-open-view="edit-appointment" data-id="${cita.idReserva || cita.id}">Editar</button>
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