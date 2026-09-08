// REGISTRO DE VISTAS INTERNAS PARA EL MODAL DE SERVICIOS
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        "new-service": {
            eyebrow: "CATÁLOGO",
            title: "Nuevo servicio",
            content: `
                <form class="form-grid" id="newServiceForm">
                    <div class="form-field">
                        <label>Nombre del servicio</label>
                        <input type="text" name="nombreService" placeholder="Ej: Sesión de rehabilitación, Masoterapia..." required>
                    </div>
                    <div class="form-field">
                        <label>Precio</label>
                        <input type="number" name="precio" placeholder="Ej: 100000" required>
                    </div>
                    <div class="form-field">
                        <label>Duración</label>
                        <input type="text" name="duracion" placeholder="Ej: 45 min">
                    </div>
                    <div class="form-field">
                        <label>Estado</label>
                        <select name="estado" required>
                            <option value="ACTIVO">Activo</option>
                            <option value="INACTIVO">Inactivo</option>
                        </select>
                    </div>
                    <div class="form-field full">
                        <label>Descripción</label>
                        <textarea name="descripcion" rows="4" placeholder="Describe brevemente en qué consiste el tratamiento..."></textarea>
                    </div>
                </form>
            `
        }
    });
}

// Elige un ícono SVG según palabras clave en el nombre del servicio (solo presentación, no toca datos)
function getServiceIcon(nombre) {
    const n = (nombre || "").toLowerCase();
    const stroke = "#1a3a63";

    if (n.includes("valoraci")) {
        return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <rect x="5" y="4" width="10" height="13" rx="1.5"/><rect x="7.5" y="2.5" width="5" height="3" rx="1"/><path d="M7.5 9h5M7.5 12h5"/>
        </svg>`;
    }
    if (n.includes("rehabilitaci") || n.includes("terapia f")) {
        return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 10h3l1.5-4 3 8 1.5-4H17"/>
        </svg>`;
    }
    if (n.includes("prescripci") || n.includes("ejercicio")) {
        return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="4" cy="10" r="1.8"/><circle cx="16" cy="10" r="1.8"/><path d="M6 10h8M4 7.5v5M16 7.5v5"/>
        </svg>`;
    }
    if (n.includes("descarga")) {
        return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M4 7c2 0 2 2 4 2s2-2 4-2 2 2 4 2"/><path d="M4 12c2 0 2 2 4 2s2-2 4-2 2 2 4 2"/>
        </svg>`;
    }
    if (n.includes("punción") || n.includes("puncion")) {
        return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 3l3 3-8 8-3 1 1-3z"/><path d="M11 6l3 3"/>
        </svg>`;
    }
    if (n.includes("neural")) {
        return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
            <path d="M11 2 5 11h4l-1 7 7-10h-4z"/>
        </svg>`;
    }
    if (n.includes("plasma") || n.includes("prp")) {
        return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round">
            <path d="M10 2c3 4 5 7 5 9.5a5 5 0 0 1-10 0C5 9 7 6 10 2z"/>
        </svg>`;
    }

    // Ícono genérico de respaldo (cruz médica) para cualquier servicio nuevo que no calce con las palabras clave
    return `<svg viewBox="0 0 20 20" fill="none" stroke="${stroke}" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">
        <path d="M10 4v12M4 10h12"/>
    </svg>`;
}

// CARGA DINÁMICA DE SERVICIOS DESDE LA BASE DE DATOS
document.addEventListener("DOMContentLoaded", () => {
    const servicesGrid = document.getElementById("serviciosGrid");
    if (servicesGrid) {
        fetch("/api/modal/servicios")
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error del servidor: " + response.status);
                }
                return response.json();
            })
            .then(servicios => {
                if (!Array.isArray(servicios) || servicios.length === 0) {
                    servicesGrid.innerHTML = "<p style='grid-column: 1 / -1; text-align:center;'>No hay servicios registrados.</p>";
                    return;
                }
                servicesGrid.innerHTML = ""; // Limpiar grid
                servicios.forEach(s => {
                    const nombreServicio = s.nombreS || s.nombre || 'Servicio sin nombre';
                    const precioServicio = s.precioIndividual !== undefined ? `$ ${s.precioIndividual.toLocaleString()}` : 'N/A';
                    const duracionServicio = s.duracionS ? `${s.duracionS} min` : (s.duracion || 'N/A');
                    const estadoServicio = s.estadoS ? 'Activo' : 'Inactivo';
                    servicesGrid.innerHTML += `
                        <article class="service-card">
                            <div class="service-icon">${getServiceIcon(nombreServicio)}</div>
                            <h3>${nombreServicio}</h3>
                            <strong>${precioServicio}</strong>
                            <p>Estado: ${estadoServicio}</p>
                            <small>Duración: ${duracionServicio}</small>
                        </article>
                    `;
                });
            })
            .catch(error => {
                console.error("Error al obtener la lista de servicios:", error);
                servicesGrid.innerHTML = "<p style='grid-column: 1 / -1; text-align:center;'>Error al cargar los servicios.</p>";
            });
    }
});