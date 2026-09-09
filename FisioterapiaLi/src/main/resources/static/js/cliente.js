// REGISTRO DE VISTAS INTERNAS PARA EL MÓDULO DE CLIENTES
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        "new-client": {
            eyebrow: "NUEVO PACIENTE",
            title: "Crear cliente",
            content: `
                <form class="form-grid" id="newClientForm">
                    <div class="form-field">
                        <label>Nombre completo</label>
                        <input type="text" name="nombre" placeholder="Ingresa el nombre completo del paciente" required>
                    </div>
                    <div class="form-field">
                        <label>N° de documento</label>
                        <input type="text" name="documento" placeholder="Número de cédula o documento de identificación" required>
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
                        <input type="text" name="eps" placeholder="Ej: Sura, Nueva EPS, Sanitas...">
                    </div>
                    <div class="form-field">
                        <label>Teléfono / WhatsApp</label>
                        <input type="tel" name="telefono" placeholder="Ej: 300 123 4567" required>
                    </div>
                    <div class="form-field">
                        <label>Correo</label>
                        <input type="email" name="email" placeholder="ejemplo@correo.com" required>
                    </div>
                    <div class="form-field">
                        <label>Contacto de emergencia</label>
                        <input type="text" name="contactoEmergencia" placeholder="Nombre y teléfono de un contacto de emergencia">
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
                        <input type="text" name="nombre" id="editNombre" placeholder="Ingresa el nombre completo del paciente" required>
                    </div>
                    <div class="form-field">
                        <label>N° de documento</label>
                        <input type="text" name="documento" id="editDocumento" placeholder="Número de cédula o documento de identificación" required>
                    </div>
                    <div class="form-field">
                        <label>EPS / Aseguradora</label>
                        <input type="text" name="eps" id="editEps" placeholder="Ej: Sura, Nueva EPS, Sanitas...">
                    </div>
                    <div class="form-field">
                        <label>Teléfono / WhatsApp</label>
                        <input type="tel" name="telefono" id="editTelefono" placeholder="Ej: 300 123 4567" required>
                    </div>
                    <div class="form-field">
                        <label>Correo</label>
                        <input type="email" name="email" id="editEmail" placeholder="ejemplo@correo.com" required>
                    </div>
                    <div class="form-field">
                        <label>Contacto de emergencia</label>
                        <input type="text" name="contactoEmergencia" id="editContactoEmergencia" placeholder="Nombre y teléfono de un contacto de emergencia">
                    </div>
                </form>
            `
        },

        "clinical-history": {
            eyebrow: "PERFIL 360",
            title: "Historia clínica",
            content: `
                <div class="internal-section">
                    <h3>Paciente</h3>
                    <div class="clinical-top">
                        <div class="info-placeholder">
                            <div>Nombre</div>
                            <span id="historyPacienteNombre">—</span>
                        </div>
                        <div class="info-placeholder">
                            <div>Documento</div>
                            <span id="historyPacienteDocumento">—</span>
                        </div>
                    </div>
                </div>

                <div class="internal-section">
                    <h3>Motivo de consulta</h3>
                    <div class="form-grid">
                        <div class="form-field full">
                            <label>Motivo principal</label>
                            <textarea rows="2" name="motivoConsulta" placeholder="Describe el motivo de la consulta..."></textarea>
                        </div>
                        <div class="form-field">
                            <label>Antecedente patológico</label>
                            <input type="text" name="antecedentePatologico" placeholder="Ej: hipertensión, cirugías previas...">
                        </div>
                        <div class="form-field">
                            <label>Ocupación</label>
                            <input type="text" name="ocupacion" placeholder="Ej: oficinista, deportista...">
                        </div>
                    </div>
                </div>

                <div class="internal-section">
                    <h3>Valoración y Evolución</h3>
                    <div class="form-grid">
                        <div class="form-field">
                            <label>Tipo de dolor</label>
                            <input type="text" name="tipoDolor" placeholder="Ej: punzante, muscular, articular...">
                        </div>
                        <div class="form-field">
                            <label>EVA</label>
                            <input type="text" name="eva" placeholder="Escala de 0 a 10">
                        </div>
                        <div class="form-field">
                            <label>Comportamiento del dolor</label>
                            <input type="text" name="comportamientoDolor" placeholder="Ej: constante, intermitente...">
                        </div>
                        <div class="form-field">
                            <label>Tensión arterial</label>
                            <input type="text" name="tensionArterial" placeholder="Ej: 120/80">
                        </div>
                        <div class="form-field">
                            <label>Frecuencia cardíaca</label>
                            <input type="text" name="frecuenciaCardiaca" placeholder="lpm">
                        </div>
                        <div class="form-field">
                            <label>Frecuencia respiratoria</label>
                            <input type="text" name="frecuenciaRespiratoria" placeholder="rpm">
                        </div>
                        <div class="form-field">
                            <label>Saturación de oxígeno</label>
                            <input type="text" name="saturacionOxigeno" placeholder="%">
                        </div>
                        <div class="form-field">
                            <label>IMC</label>
                            <input type="text" name="imc" placeholder="Ej: 24.5">
                        </div>
                    </div>
                </div>

                <div class="internal-section">
                    <h3>Objetivo y evolución</h3>
                    <div class="form-grid">
                        <div class="form-field full">
                            <label>Objetivo del tratamiento</label>
                            <textarea rows="3" name="objetivo" placeholder="Ej: reducir dolor, recuperar movilidad..."></textarea>
                        </div>
                        <div class="form-field full">
                            <label>Evolución / observaciones</label>
                            <textarea rows="3" name="evolucion" placeholder="Notas de seguimiento entre sesiones..."></textarea>
                        </div>
                    </div>
                </div>
            `
        }
    });
}

// OBTENER CABECERAS CSRF PARA EVITAR ERRORES 403 EN POST/PUT/DELETE
function getFetchHeaders() {
    const headers = { "Content-Type": "application/json" };
    const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
    const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }
    return headers;
}

// VARIABLE GLOBAL PARA ALMACENAR LOS CLIENTES
let todosLosClientes = [];

// CARGA AUTOMÁTICA Y GESTIÓN DE EVENTOS
document.addEventListener("DOMContentLoaded", () => {
    cargarClientes();

    // ==========================================
    // VINCULACIÓN DIRECTA DE LOS FILTROS
    // ==========================================
    const btnFiltrar = document.getElementById("btnFiltrar");
    if (btnFiltrar) {
        btnFiltrar.onclick = (e) => {
            e.preventDefault();
            aplicarFiltrosClientes();
        };
    }

    // BOTÓN LIMPIAR FILTROS
    const btnLimpiar = document.getElementById("btnLimpiar");
    if (btnLimpiar) {
        btnLimpiar.addEventListener("click", (e) => {
            e.preventDefault();
            const inputNombre = document.getElementById("filtroNombre");
            const selectEstado = document.getElementById("filtroEstado");

            if (inputNombre) inputNombre.value = "";
            if (selectEstado) selectEstado.value = "";

            aplicarFiltrosClientes();
        });
    }

    // FILTRADO EN TIEMPO REAL AL ESCRIBIR EN EL BUSCADOR
    const filtroNombre = document.getElementById("filtroNombre");
    if (filtroNombre) {
        filtroNombre.addEventListener("input", aplicarFiltrosClientes);
        filtroNombre.addEventListener("keyup", (e) => {
            if (e.key === "Enter") aplicarFiltrosClientes();
        });
    }

    // FILTRADO AUTOMÁTICO AL CAMBIAR EL ESTADO
    const filtroEstado = document.getElementById("filtroEstado");
    if (filtroEstado) {
        filtroEstado.addEventListener("change", aplicarFiltrosClientes);
    }

    // DELEGACIÓN DE EVENTOS GLOBALES (Historial, Editar, Eliminar)
    document.addEventListener("click", event => {
        if (event.target.closest('[data-open-view="clinical-history"]')) {
            const btn = event.target.closest('[data-open-view="clinical-history"]');
            setTimeout(() => {
                const nombreSpan = document.getElementById("historyPacienteNombre");
                if (nombreSpan) nombreSpan.textContent = btn.dataset.nombre || "—";

                const documentoSpan = document.getElementById("historyPacienteDocumento");
                if (documentoSpan) documentoSpan.textContent = btn.dataset.documento || "—";
            }, 50);
        }

        // EDICIÓN DE CLIENTE (Carga la información actual en el formulario del modal)
        const btnEdit = event.target.closest('.btn-edit');
        if (btnEdit) {
            setTimeout(() => {
                const inputId = document.getElementById("editIdUsuario");
                const inputNombre = document.getElementById("editNombre");
                const inputDocumento = document.getElementById("editDocumento");
                const inputEps = document.getElementById("editEps");
                const inputTelefono = document.getElementById("editTelefono");
                const inputEmail = document.getElementById("editEmail");
                const inputContacto = document.getElementById("editContactoEmergencia");

                if (inputId) inputId.value = btnEdit.dataset.id || "";
                if (inputNombre) inputNombre.value = btnEdit.dataset.nombre || "";
                if (inputDocumento) inputDocumento.value = btnEdit.dataset.documento || "";
                if (inputEps) inputEps.value = btnEdit.dataset.eps || "";
                if (inputTelefono) inputTelefono.value = btnEdit.dataset.telefono || "";
                if (inputEmail) inputEmail.value = btnEdit.dataset.email || "";
                if (inputContacto) inputContacto.value = btnEdit.dataset.contacto || "";
            }, 50);
        }

        if (event.target.classList.contains("btn-delete")) {
            const idPaciente = event.target.dataset.id;

            const confirmFunc = typeof showConfirmDialog === "function"
                ? showConfirmDialog("¿Estás seguro de que deseas eliminar este cliente?")
                : Promise.resolve(confirm("¿Estás seguro de que deseas eliminar este cliente?"));

            confirmFunc.then(confirmado => {
                if (!confirmado) return;

                fetch(`/api/modal/pacientes/${idPaciente}`, {
                    method: "DELETE",
                    headers: getFetchHeaders()
                })
                    .then(async response => {
                        if (response.ok) {
                            if (typeof mostrarNotificacionPersonalizada === "function") {
                                mostrarNotificacionPersonalizada("Cliente eliminado exitosamente.", "success");
                            } else {
                                alert("Cliente eliminado exitosamente.");
                            }
                            cargarClientes();
                        } else {
                            const mensajeError = await response.text();
                            if (response.status === 409 || mensajeError.includes("constraint") || mensajeError.includes("foreign key") || mensajeError.includes("Reservation") || mensajeError.includes("citas")) {
                                if (typeof mostrarNotificacionPersonalizada === "function") {
                                    mostrarNotificacionPersonalizada("No es posible eliminar este cliente porque tiene citas o reservas asociadas en la agenda. Debes eliminar sus citas primero.", "error");
                                } else {
                                    alert("No es posible eliminar este cliente porque tiene citas o reservas asociadas en la agenda.");
                                }
                            } else {
                                if (typeof mostrarNotificacionPersonalizada === "function") {
                                    mostrarNotificacionPersonalizada("No se pudo eliminar el cliente: " + mensajeError, "error");
                                } else {
                                    alert("No se pudo eliminar el cliente: " + mensajeError);
                                }
                            }
                        }
                    })
                    .catch(err => {
                        console.error("Error de red al eliminar:", err);
                        if (typeof mostrarNotificacionPersonalizada === "function") {
                            mostrarNotificacionPersonalizada("Ocurrió un error de conexión al intentar eliminar el cliente.", "error");
                        } else {
                            alert("Ocurrió un error de conexión al intentar eliminar el cliente.");
                        }
                    });
            });
        }
    });
});

// FUNCIÓN PARA CARGAR CLIENTES DESDE LA API
function cargarClientes() {
    fetch("/api/modal/pacientes", {
        headers: getFetchHeaders()
    })
        .then(response => response.json())
        .then(pacientes => {
            if (!pacientes || pacientes.length === 0) {
                todosLosClientes = [];
                renderizarTablaClientes([]);
                return;
            }

            // Filtrar únicamente los que tienen rol CLIENTE
            todosLosClientes = pacientes.filter(p => p.rol === "CLIENTE");
            aplicarFiltrosClientes();
        })
        .catch(error => {
            console.error("Error al cargar lista de clientes:", error);
            renderizarTablaClientes([]);
        });
}

// FUNCIÓN DE FILTRADO EN MEMORIA
function aplicarFiltrosClientes() {
    const textoBusqueda = document.getElementById("filtroNombre")?.value || "";
    const valEstado = document.getElementById("filtroEstado")?.value || "";

    const normalizeText = (str) => {
        if (!str) return "";
        return str.toString().normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase().trim();
    };

    const searchNormal = normalizeText(textoBusqueda);
    const estadoSelectNormal = normalizeText(valEstado);

    const clientesFiltrados = todosLosClientes.filter(p => {
        const nombre = normalizeText(p.nombre);
        const documento = normalizeText(p.documento);
        const estadoCliente = normalizeText(p.estado || "activo");

        const cumpleTexto = searchNormal === "" || nombre.includes(searchNormal) || documento.includes(searchNormal);
        const cumpleEstado = estadoSelectNormal === "" || estadoCliente === estadoSelectNormal;

        return cumpleTexto && cumpleEstado;
    });

    renderizarTablaClientes(clientesFiltrados);
}

// RENDERIZAR TABLA DE CLIENTES
function renderizarTablaClientes(clientesArray) {
    const tableBody = document.querySelector(".wide-table tbody");
    if (!tableBody) return;

    if (!Array.isArray(clientesArray) || clientesArray.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='7' style='text-align:center;'>No se encontraron clientes.</td></tr>";
        return;
    }

    tableBody.innerHTML = "";

    clientesArray.forEach((p, idx) => {
        const inicial = (p.nombre || "S").trim().charAt(0).toUpperCase();
        const avatarColor = idx % 2 === 0 ? "navy" : "gold";
        const referidos = p.contadorReferidos !== undefined ? p.contadorReferidos : 0;
        const estadoTexto = p.estado || "Activo";

        tableBody.innerHTML += `
            <tr>
                <td>
                    <div class="patient-cell">
                        <span class="avatar-circle ${avatarColor}">${inicial}</span>
                        <span>${p.nombre || 'Sin nombre'}</span>
                    </div>
                </td>
                <td>${p.documento || 'N/A'}</td>
                <td>${p.telefono || 'N/A'}</td>
                <td>${p.eps || '-'}</td>
                <td><span class="ref-badge${referidos > 0 ? ' has-refs' : ''}">${referidos}</span></td>
                <td><span class="status confirmed">${estadoTexto}</span></td>
                <td>
                    <button class="table-action" data-open-view="clinical-history" 
                        data-id="${p.idUsuario}" 
                        data-nombre="${p.nombre || ''}" 
                        data-documento="${p.documento || ''}">Historial clínico</button>
                    <button class="table-action btn-edit" data-open-view="edit-client" 
                        data-id="${p.idUsuario}" 
                        data-nombre="${p.nombre || ''}" 
                        data-documento="${p.documento || ''}" 
                        data-eps="${p.eps || ''}" 
                        data-telefono="${p.telefono || ''}" 
                        data-email="${p.email || ''}" 
                        data-contacto="${p.contactoEmergencia || ''}" style="margin-left: 8px;">Editar</button>
                    <button class="table-action btn-delete" data-id="${p.idUsuario}" style="color: #d9534f; margin-left: 8px;">Eliminar</button>
                </td>
            </tr>
        `;
    });
}