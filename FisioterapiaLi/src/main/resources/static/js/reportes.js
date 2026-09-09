// REGISTRO DE VISTAS INTERNAS PARA EL MÓDULO DE REPORTES
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        // Vistas internas de filtros avanzados o auditoría
    });
}

document.addEventListener("DOMContentLoaded", () => {
    cargarHistorialYMetricas();

    // VINCULACIÓN DEL BOTÓN DE EXPORTAR EXCEL
    const btnExportar = document.getElementById("btnExportarExcel");
    if (btnExportar) {
        btnExportar.addEventListener("click", () => {
            exportarTablaAExcel("historialGrid", "Historial_Auditoria_FisioterapiaLi.xls");
        });
    }
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

// FUNCIÓN PARA EXPORTAR LA TABLA DE HISTORIAL A EXCEL
function exportarTablaAExcel(idTabla, nombreArchivo) {
    const tabla = document.getElementById(idTabla);
    if (!tabla) {
        if (typeof mostrarNotificacionPersonalizada === "function") {
            mostrarNotificacionPersonalizada("No se encontró la tabla para exportar.", "error");
        }
        return;
    }

    let contenidoHTML = `
        <html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40">
        <head>
            <meta charset="UTF-8">
            <style>
                table { border-collapse: collapse; width: 100%; }
                th, td { border: 1px solid #cbd5e1; padding: 8px 12px; font-family: 'Poppins', sans-serif; font-size: 12px; text-align: left; }
                th { background-color: #1e293b; color: #ffffff; font-weight: bold; }
            </style>
        </head>
        <body>
            <table>
                <thead>
                    <tr>
                        <th>Fecha y Hora</th>
                        <th>Módulo / Origen</th>
                        <th>Acción Realizada</th>
                        <th>Usuario</th>
                    </tr>
                </thead>
                <tbody>
    `;

    const filas = tabla.querySelectorAll("tr");

    if (filas.length === 0 || (filas.length === 1 && filas[0].querySelector("td[colspan]"))) {
        if (typeof mostrarNotificacionPersonalizada === "function") {
            mostrarNotificacionPersonalizada("No hay registros en el historial para exportar.", "error");
        }
        return;
    }

    filas.forEach(fila => {
        const celdas = fila.querySelectorAll("td");
        if (celdas.length >= 4) {
            const fecha = celdas[0].innerText;
            const origen = celdas[1].innerText;
            const accion = celdas[2].innerText;
            const usuario = celdas[3].innerText;

            contenidoHTML += `
                <tr>
                    <td>${fecha}</td>
                    <td>${origen}</td>
                    <td>${accion}</td>
                    <td>${usuario}</td>
                </tr>
            `;
        }
    });

    contenidoHTML += `
                </tbody>
            </table>
        </body>
        </html>
    `;

    const blob = new Blob([contenidoHTML], { type: "application/vnd.ms-excel;charset=utf-8;" });
    const url = URL.createObjectURL(blob);

    const enlaceTemporal = document.createElement("a");
    enlaceTemporal.href = url;
    enlaceTemporal.download = nombreArchivo;
    document.body.appendChild(enlaceTemporal);
    enlaceTemporal.click();
    document.body.removeChild(enlaceTemporal);

    if (typeof mostrarNotificacionPersonalizada === "function") {
        mostrarNotificacionPersonalizada("¡Historial exportado a Excel exitosamente!", "success");
    }
}