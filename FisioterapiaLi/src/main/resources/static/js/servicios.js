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
                        <input type="text" name="nombreService" required>
                    </div>
                    <div class="form-field">
                        <label>Precio</label>
                        <input type="number" name="precio" required>
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
                        <textarea name="descripcion" rows="4" placeholder="Detalles del tratamiento..."></textarea>
                    </div>
                </form>
            `
        }
    });
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
                            <div class="service-icon"></div>
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