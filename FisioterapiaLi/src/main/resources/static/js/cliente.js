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
                        <label>EPS / Aseguradora</label>
                        <input type="text" name="eps">
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
                        <label>Documento de quien lo refirió</label>
                        <input type="text" name="referidoPor" placeholder="N° de documento del referente">
                    </div>
                </form>
            `
        },

        "edit-client": {
            eyebrow: "EDICIÓN DE PACIENTE",
            title: "Modificar cliente",
            content: `
                <form class="form-grid" id="editClientForm">
                    <input type="hidden" name="idUsuario" id="editIdUsuario">
                    <div class="form-field">
                        <label>Nombre completo</label>
                        <input type="text" name="nombre" id="editNombre" required>
                    </div>
                    <div class="form-field">
                        <label>N° de documento</label>
                        <input type="text" name="documento" id="editDocumento" required>
                    </div>
                    <div class="form-field">
                        <label>EPS / Aseguradora</label>
                        <input type="text" name="eps" id="editEps">
                    </div>
                    <div class="form-field">
                        <label>Teléfono / WhatsApp</label>
                        <input type="tel" name="telefono" id="editTelefono" required>
                    </div>
                    <div class="form-field">
                        <label>Correo</label>
                        <input type="email" name="email" id="editEmail" required>
                    </div>
                    <div class="form-field">
                        <label>Contacto de emergencia</label>
                        <input type="text" name="contactoEmergencia" id="editContactoEmergencia">
                    </div>
                </form>
            `
        },

        "clinical-history": {
            eyebrow: "PERFIL 360",
            title: "Historia clínica",
            content: `
                <div class="internal-section">
                    <h3>Valoración y Evolución</h3>
                    <div class="form-grid">
                        <div class="form-field"><label>Tipo de dolor</label><input type="text" name="tipoDolor"></div>
                        <div class="form-field"><label>EVA</label><input type="text" name="eva"></div>
                    </div>
                </div>
            `
        }
    });
}

// Carga automática de la lista y gestión de eventos en la tabla
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

                const clientesFiltrados = pacientes.filter(p => p.rol === "CLIENTE");

                if (clientesFiltrados.length === 0) {
                    tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>No se encontraron clientes activos.</td></tr>";
                    return;
                }

                tableBody.innerHTML = "";

                clientesFiltrados.forEach(p => {
                    tableBody.innerHTML += `
                        <tr>
                            <td>${p.nombre || 'Sin nombre'}</td>
                            <td>${p.documento || 'N/A'}</td>
                            <td>${p.telefono || 'N/A'}</td>
                            <td>${p.eps || '-'}</td>
                            <td>${p.contadorReferidos !== undefined ? p.contadorReferidos : 0}</td>
                            <td><span class="status">Activo</span></td>
                            <td>
                                <button class="table-action btn-edit" data-open-view="edit-client" 
                                    data-id="${p.idUsuario}" 
                                    data-nombre="${p.nombre || ''}" 
                                    data-documento="${p.documento || ''}" 
                                    data-eps="${p.eps || ''}" 
                                    data-telefono="${p.telefono || ''}" 
                                    data-email="${p.email || ''}" 
                                    data-contacto="${p.contactoEmergencia || ''}">Editar</button>
                                <button class="table-action btn-delete" data-id="${p.idUsuario}" style="color: #d9534f; margin-left: 8px;">Eliminar</button>
                            </td>
                        </tr>
                    `;
                });
            })
            .catch(error => console.error("Error al cargar lista de clientes:", error));
    }

    // Delegación de eventos para los botones de la tabla dinámicos
    document.addEventListener("click", event => {
        // Lógica de llenado para el modal de edición
        if (event.target.classList.contains("btn-edit")) {
            const btn = event.target;
            setTimeout(() => {
                document.getElementById("editIdUsuario").value = btn.dataset.id;
                document.getElementById("editNombre").value = btn.dataset.nombre;
                document.getElementById("editDocumento").value = btn.dataset.documento;
                document.getElementById("editEps").value = btn.dataset.eps;
                document.getElementById("editTelefono").value = btn.dataset.telefono;
                document.getElementById("editEmail").value = btn.dataset.email;
                document.getElementById("editContactoEmergencia").value = btn.dataset.contacto;
            }, 50);
        }

        // Lógica de eliminación (Delete)
        if (event.target.classList.contains("btn-delete")) {
            const idPaciente = event.target.dataset.id;
            if (confirm("¿Estás seguro de que deseas eliminar este cliente?")) {
                fetch(`/api/modal/pacientes/${idPaciente}`, {
                    method: "DELETE"
                })
                    .then(response => {
                        if (response.ok) {
                            alert("Cliente eliminado exitosamente.");
                            window.location.reload();
                        } else {
                            response.text().then(msg => alert("Error al eliminar: " + msg));
                        }
                    })
                    .catch(err => console.error("Error de red al eliminar:", err));
            }
        }
    });
});