if (typeof registerInternalViews === "function") {
    registerInternalViews({
        "new-client": {
            eyebrow: "NUEVO PACIENTE",
            title: "Crear cliente",
            content: `
                <form class="form-grid" id="newClientForm">
                    <div class="form-field">
                        <label>Nombre completo</label>
                        <input type="text" name="nombre" required>
                    </div>
                    <div class="form-field">
                        <label>N° de documento</label>
                        <input type="text" name="documento" required>
                    </div>
                    <div class="form-field">
                        <label>Tipo de documento</label>
                        <select name="tipoDocumento">
                            <option value="">Seleccionar...</option>
                            <option value="CC">Cédula de Ciudadanía (C.C.)</option>
                            <option value="TI">Tarjeta de Identidad (T.I.)</option>
                            <option value="CE">Cédula de Extranjería (C.E.)</option>
                            <option value="PP">Pasaporte</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label>Fecha de nacimiento</label>
                        <input type="date" name="fechaNacimiento">
                    </div>
                    <div class="form-field">
                        <label>Género</label>
                        <select name="genero">
                            <option value="">Seleccionar...</option>
                            <option value="Femenino">Femenino</option>
                            <option value="Masculino">Masculino</option>
                            <option value="Otro">Otro / Prefiero no decir</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label>Teléfono / WhatsApp</label>
                        <input type="tel" name="telefono" required>
                    </div>
                    <div class="form-field">
                        <label>Correo</label>
                        <input type="email" name="email" required>
                    </div>
                    <div class="form-field">
                        <label>Contacto de emergencia</label>
                        <input type="text" name="contactoEmergencia">
                    </div>
                    <div class="form-field">
                        <label>¿Quién lo refirió?</label>
                        <input type="text" name="referidoPor">
                    </div>
                </form>
            `
        },

        "clinical-history": {
            eyebrow: "PERFIL 360",
            title: "Historia clínica",
            content: `
                <div class="clinical-top">
                    <div class="info-placeholder">
                        <div></div>
                        <span></span>
                    </div>
                    <div class="info-placeholder">
                        <div></div>
                        <span></span>
                    </div>
                </div>

                <div class="internal-section">
                    <h3>Datos clínicos</h3>
                    <div class="placeholder-line"></div>
                    <div class="placeholder-line short"></div>
                </div>

                <div class="internal-section">
                    <h3>Motivo principal</h3>
                    <div class="placeholder-line"></div>
                    <div class="placeholder-line short"></div>
                </div>

                <div class="internal-section">
                    <h3>Valoración</h3>
                    <div class="form-grid">
                        <div class="form-field">
                            <label>Tipo de dolor</label>
                            <input type="text" name="tipoDolor">
                        </div>
                        <div class="form-field">
                            <label>Tensión arterial</label>
                            <input type="text" name="tensionArterial">
                        </div>
                        <div class="form-field">
                            <label>Frecuencia cardíaca</label>
                            <input type="text" name="frecuenciaCardiaca">
                        </div>
                        <div class="form-field">
                            <label>Frecuencia respiratoria</label>
                            <input type="text" name="frecuenciaRespiratoria">
                        </div>
                        <div class="form-field">
                            <label>IMC</label>
                            <input type="text" name="imc">
                        </div>
                        <div class="form-field">
                            <label>EVA</label>
                            <input type="text" name="eva">
                        </div>
                    </div>
                </div>

                <div class="internal-section">
                    <h3>Objetivo y evolución</h3>
                    <div class="form-grid">
                        <div class="form-field full">
                            <label>Objetivo</label>
                            <textarea rows="3" name="objetivo"></textarea>
                        </div>
                        <div class="form-field full">
                            <label>Evolución / observaciones</label>
                            <textarea rows="4" name="evolucion"></textarea>
                        </div>
                    </div>
                </div>
            `
        }
    });
}

// Carga automática de la lista filtrada de clientes
document.addEventListener("DOMContentLoaded", () => {
    const tableBody = document.querySelector(".wide-table tbody");

    if (tableBody) {
        fetch("/api/modal/pacientes")
            .then(response => response.json())
            .then(pacientes => {
                if (!pacientes || pacientes.length === 0) {
                    tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>No hay clientes registrados.</td></tr>";
                    return;
                }

                // Filtrar para mostrar únicamente registros con rol CLIENTE
                const clientesFiltrados = pacientes.filter(p => p.rol === "CLIENTE");

                if (clientesFiltrados.length === 0) {
                    tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>No se encontraron clientes registrados.</td></tr>";
                    return;
                }

                tableBody.innerHTML = ""; // Limpiar filas estáticas de plantilla

                clientesFiltrados.forEach(p => {
                    tableBody.innerHTML += `
                        <tr>
                            <td>${p.nombre || 'Sin nombre'}</td>
                            <td>${p.documento || 'N/A'}</td>
                            <td>${p.telefono || 'N/A'}</td>
                            <td>-</td>
                            <td>${p.contadorReferidos !== undefined ? p.contadorReferidos : 0}</td>
                            <td><span class="status">Activo</span></td>
                            <td>
                                <button class="table-action" data-open-view="clinical-history">Ver historial clínico</button>
                            </td>
                        </tr>
                    `;
                });
            })
            .catch(error => console.error("Error al obtener la lista de clientes:", error));
    }
});