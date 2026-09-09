// REGISTRO DE VISTAS INTERNAS PARA EL MÓDULO DE REPORTES
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        // Vistas internas de filtros avanzados o auditoría
    });
}

document.addEventListener("DOMContentLoaded", () => {
    cargarHistorialYMetricas();
});

function cargarHistorialYMetricas() {
    const historialGrid = document.getElementById("historialGrid");

    // 1. Cargar el historial y auditoría
    if (historialGrid) {
        fetch("/api/modal/reportes/historial")
            .then(response => {
                if (!response.ok) throw new Error("Error del servidor: " + response.status);
                return response.json();
            })
            .then(registros => {
                if (!Array.isArray(registros) || registros.length === 0) {
                    historialGrid.innerHTML = "<tr><td colspan='4' style='text-align:center; padding: 20px;'>No hay registros en el historial.</td></tr>";
                    return;
                }

                historialGrid.innerHTML = "";
                registros.forEach(r => {
                    const fechaFormateada = r.fechaHora ? r.fechaHora.replace("T", " ") : 'N/A';
                    const origenAccion = r.origen || 'GENERAL';
                    const descripcionAccion = r.accion || 'SIN ACCIÓN';
                    const nombreUsuario = r.usuario && r.usuario.nombre ? r.usuario.nombre : 'Administrador';

                    historialGrid.innerHTML += `
                        <tr>
                            <td>${fechaFormateada}</td>
                            <td><strong>${origenAccion}</strong></td>
                            <td>
                                <span style="padding: 4px 10px; border-radius: 6px; background: #eef2f7; color: #1a3a63; font-weight: 600; font-size: 12px;">
                                    ${descripcionAccion}
                                </span>
                            </td>
                            <td>${nombreUsuario}</td>
                        </tr>
                    `;
                });
            })
            .catch(error => {
                console.error("Error al obtener la lista del historial:", error);
                historialGrid.innerHTML = "<tr><td colspan='4' style='text-align:center; padding: 20px; color: #d9534f;'>Error al cargar los reportes del sistema.</td></tr>";
                if (typeof mostrarNotificacionPersonalizada === "function") {
                    mostrarNotificacionPersonalizada("No se pudo cargar el historial de actividad del sistema.", "error");
                }
            });
    }

    // 2. Cargar datos reales para calcular las métricas de los paneles superiores
    Promise.all([
        fetch("/api/modal/citas").then(res => res.ok ? res.json() : []),
        fetch("/api/modal/pacientes").then(res => res.ok ? res.json() : [])
    ])
        .then(([citas, pacientes]) => {
            calcularYMostrarMetricas(citas, pacientes);
        })
        .catch(err => {
            console.error("Error al cargar datos para métricas:", err);
            if (typeof mostrarNotificacionPersonalizada === "function") {
                mostrarNotificacionPersonalizada("Error al sincronizar las métricas y reportes.", "error");
            }
        });
}

function calcularYMostrarMetricas(citas, pacientes) {
    const fechaActual = new Date();
    const mesActual = fechaActual.getMonth();
    const anioActual = fechaActual.getFullYear();

    let ingresosMes = 0;
    let citasAtendidas = 0;
    let cancelaciones = 0;
    let pacientesNuevos = 0;

    if (Array.isArray(citas)) {
        citas.forEach(cita => {
            const estado = (cita.estadoCita || cita.estado || "").toUpperCase();

            if (estado === "CONFIRMADA" || estado === "ATENDIDA" || estado === "FINALIZADA") {
                citasAtendidas++;
            }

            if (estado === "CANCELADA") {
                cancelaciones++;
            }

            if (cita.fechaInicio) {
                const fechaCita = new Date(cita.fechaInicio);
                if (fechaCita.getMonth() === mesActual && fechaCita.getFullYear() === anioActual) {
                    if (cita.pagoConfirmado || estado === "CONFIRMADA" || estado === "ATENDIDA" || estado === "FINALIZADA") {
                        const valorServicio = cita.servicio
                            ? Number(cita.servicio.precioIndividual || cita.servicio.precio || cita.servicio.costo || 0)
                            : 0;
                        ingresosMes += valorServicio;
                    }
                }
            }
        });
    }

    if (Array.isArray(pacientes)) {
        pacientes.forEach(p => {
            if (p.rol === "CLIENTE") {
                pacientesNuevos++;
            }
        });
    }

    // Actualización de los elementos en el DOM
    const elIngresos = document.getElementById("statIngresosMes");
    const elAtendidas = document.getElementById("statCitasAtendidas");
    const elCanceladas = document.getElementById("statCancelacionesReportes");
    const elNuevos = document.getElementById("statPacientesNuevos");

    if (elIngresos) {
        elIngresos.textContent = "$" + ingresosMes.toLocaleString("es-CO");
    }
    if (elAtendidas) {
        elAtendidas.textContent = citasAtendidas;
    }
    if (elCanceladas) {
        elCanceladas.textContent = cancelaciones;
    }
    if (elNuevos) {
        elNuevos.textContent = pacientesNuevos;
    }
}