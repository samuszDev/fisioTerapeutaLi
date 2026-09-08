// REGISTRO DE VISTAS INTERNAS PARA EL MÓDULO DE REPORTES (Si se requiere algún filtro o detalle adicional)
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        // Puedes agregar vistas de filtros avanzados o detalles de auditoría aquí si lo requieres luego
    });
}

// CARGA DINÁMICA DEL HISTORIAL Y AUDITORÍA DE REGISTROS
document.addEventListener("DOMContentLoaded", () => {
    const historialGrid = document.getElementById("historialGrid");

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
            });
    }
});