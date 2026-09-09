// Asegúrate de que common.js se carga antes en tu HTML para que registerInternalViews esté disponible
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
                            <option value="">Seleccionar paciente...</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label>Servicio</label>
                        <select name="idServicio" id="selectServicio" required>
                            <option value="">Seleccionar servicio...</option>
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
                            <option value="">Seleccionar sede...</option>
                            <option value="Tunja">Sede Tunja (Lunes a Viernes)</option>
                            <option value="Turmequé">Sede Turmequé (Sábados y Domingos)</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label>Estado de pago</label>
                        <select name="estadoPago" required>
                            <option value="PAGADO">Pago anticipado 100%</option>
                            <option value="PENDIENTE">Pendiente</option>
                        </select>
                    </div>
                    <div class="form-field full">
                        <label>Observaciones</label>
                        <textarea name="observaciones" rows="4" placeholder="Notas clínicas o detalles de la reserva..."></textarea>
                    </div>
                </form>
            `
        },

        "reports": {
            eyebrow: "VISIÓN ADMINISTRATIVA",
            title: "Reportes",
            content: `
                <div class="internal-section">
                    <h3>Indicadores</h3>
                    <div class="placeholder-line"></div>
                    <div class="placeholder-line"></div>
                    <div class="placeholder-line short"></div>
                </div>

                <div class="internal-section">
                    <h3>Resumen</h3>
                    <div class="placeholder-line"></div>
                    <div class="placeholder-line short"></div>
                </div>
            `
        }
    });
}

// Función auxiliar para integrar las cabeceras CSRF en las peticiones AJAX del dashboard
function getFetchHeaders() {
    const headers = { "Content-Type": "application/json" };
    const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }
    return headers;
}

// Carga de métricas y agenda de hoy para el Dashboard
document.addEventListener("DOMContentLoaded", () => {
    const elCitasHoy = document.getElementById("statCitasHoy");
    const elPacientesActivos = document.getElementById("statPacientesActivos");
    const elPendientesPago = document.getElementById("statPendientesPago");
    const elReferidos = document.getElementById("statReferidos");

    // 1. Obtener indicadores generales
    if (elCitasHoy) {
        fetch("/api/modal/dashboard/stats", {
            headers: getFetchHeaders()
        })
            .then(res => res.ok ? res.json() : Promise.reject(res.status))
            .then(data => {
                elCitasHoy.textContent = data.citasHoy ?? 0;
                if (elPacientesActivos) elPacientesActivos.textContent = data.pacientesActivos ?? 0;
                if (elPendientesPago) elPendientesPago.textContent = data.pendientesPago ?? 0;
                if (elReferidos) elReferidos.textContent = data.referidosCompletados ?? 0;
            })
            .catch(err => console.error("Error cargando estadísticas:", err));
    }

    // 2. Cargar tabla de la agenda del día
    const agendaHoyBody = document.getElementById("agendaHoyRows");
    if (agendaHoyBody) {
        fetch("/api/modal/citas", {
            headers: getFetchHeaders()
        })
            .then(res => res.ok ? res.json() : Promise.reject(res.status))
            .then(citas => {
                const hoyStr = new Date().toISOString().substring(0, 10);
                const citasHoy = citas.filter(c => c.fechaInicio && c.fechaInicio.startsWith(hoyStr));

                if (citasHoy.length === 0) {
                    agendaHoyBody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>No hay citas agendadas para hoy.</td></tr>";
                    return;
                }

                agendaHoyBody.innerHTML = "";
                citasHoy.forEach(c => {
                    const hora = c.fechaInicio.substring(11, 16);
                    const paciente = c.usuario ? c.usuario.nombre : 'N/A';
                    const servicio = c.servicio ? (c.servicio.nombreS || c.servicio.nombre || 'Servicio') : 'N/A';
                    const estado = c.estadoCita || 'Confirmada';

                    let estadoClass = "confirmed";
                    if (estado.toUpperCase() === "PENDIENTE") estadoClass = "pending";
                    if (estado.toUpperCase() === "CANCELADA") estadoClass = "cancelled";

                    agendaHoyBody.innerHTML += `
                        <tr>
                            <td>${hora}</td>
                            <td>${paciente}</td>
                            <td>${servicio}</td>
                            <td><span class="status ${estadoClass}">${estado}</span></td>
                        </tr>
                    `;
                });
            })
            .catch(err => {
                console.error("Error al cargar agenda de hoy:", err);
                agendaHoyBody.innerHTML = "<tr><td colspan='4' style='text-align:center;'>Error al obtener citas de hoy.</td></tr>";
            });
    }
});

// LÓGICA PARA CARGAR DATOS Y GUARDAR LA CITA AL ABRIR EL MODAL
document.addEventListener("click", event => {
    const trigger = event.target.closest('[data-open-view="new-appointment"]');

    if (trigger) {
        // 1. Cargar lista de Pacientes en el select
        fetch("/api/modal/pacientes", {
            headers: getFetchHeaders()
        })
            .then(res => res.json())
            .then(pacientes => {
                const selectP = document.getElementById("selectPaciente");
                if (selectP) {
                    selectP.innerHTML = '<option value="">Seleccionar paciente...</option>';
                    pacientes.forEach(p => {
                        selectP.innerHTML += `<option value="${p.idUsuario}">${p.nombre} (${p.documento || 'S/D'})</option>`;
                    });
                }
            })
            .catch(err => console.error("Error al cargar pacientes", err));

        // 2. Cargar lista de Servicios en el select
        fetch("/api/modal/servicios", {
            headers: getFetchHeaders()
        })
            .then(res => res.json())
            .then(servicios => {
                const selectS = document.getElementById("selectServicio");
                if (selectS) {
                    selectS.innerHTML = '<option value="">Seleccionar servicio...</option>';
                    servicios.forEach(s => {
                        const nombreReal = s.nombreS || s.nombre || 'Servicio sin nombre';
                        const idReal = s.idServicio || s.id || '';
                        selectS.innerHTML += `<option value="${idReal}">${nombreReal}</option>`;
                    });
                }
            })
            .catch(err => console.error("Error al cargar servicios", err));

        // 3. Configurar el botón "Finalizado" para ejecutar el POST al backend con CSRF
        setTimeout(() => {
            const finishBtn = document.getElementById("modalFinish");
            if (finishBtn) {
                const newFinishBtn = finishBtn.cloneNode(true);
                finishBtn.parentNode.replaceChild(newFinishBtn, finishBtn);

                newFinishBtn.addEventListener("click", () => {
                    const form = document.getElementById("newAppointmentForm");
                    if (!form) return;

                    if (!form.checkValidity()) {
                        form.reportValidity();
                        return;
                    }

                    const formData = new FormData(form);
                    const data = Object.fromEntries(formData.entries());

                    fetch("/api/modal/citas", {
                        method: "POST",
                        headers: getFetchHeaders(),
                        body: JSON.stringify(data)
                    })
                        .then(async response => {
                            if (response.ok) {
                                if (typeof mostrarNotificacionPersonalizada === "function") {
                                    mostrarNotificacionPersonalizada("¡Cita agendada correctamente!", "success");
                                } else {
                                    alert("¡Cita agendada correctamente!");
                                }
                                setTimeout(() => window.location.reload(), 1500);
                            } else {
                                const errorMsg = await response.text();
                                if (typeof mostrarNotificacionPersonalizada === "function") {
                                    mostrarNotificacionPersonalizada("Error al agendar: " + errorMsg, "error");
                                } else {
                                    alert("Error al agendar: " + errorMsg);
                                }
                            }
                        })
                        .catch(error => {
                            console.error("Error de red:", error);
                            if (typeof mostrarNotificacionPersonalizada === "function") {
                                mostrarNotificacionPersonalizada("Error al conectar con el servidor.", "error");
                            } else {
                                alert("Error al conectar con el servidor.");
                            }
                        });
                });
            }
        }, 100);
    }
});