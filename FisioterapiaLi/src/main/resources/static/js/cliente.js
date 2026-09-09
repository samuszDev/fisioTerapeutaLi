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
        <form id="clinicalHistoryForm">
        <input type="hidden" name="idUsuario" id="historyIdUsuario">

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
                <div class="info-placeholder">
                    <div>EPS / Aseguradora</div>
                    <span id="historyPacienteEps">—</span>
                </div>
            </div>
        </div>

        <div class="internal-section">
            <h3>Motivo de consulta</h3>
            <div class="form-grid">
                <div class="form-field">
                    <label>Motivo principal (categoría)</label>
                    <select name="motivoPrincipalConsulta">
                        <option value="">Seleccionar...</option>
                        <option value="DOLOR_AGUDO_CRONICO">Dolor agudo / crónico</option>
                        <option value="RECUPERACION_POSTQUIRURGICA">Recuperación postquirúrgica</option>
                        <option value="LESION_DEPORTIVA_SOBRECARGA">Lesión deportiva / Sobrecarga muscular</option>
                        <option value="MANTENIMIENTO_DESCARGA_POSTENTRENAMIENTO">Mantenimiento / Descarga postentrenamiento</option>
                        <option value="PROBLEMA_POSTURAL_ERGONOMIA">Problema postural / Ergonomía</option>
                        <option value="DISFUNCION_ARTICULAR">Disfunción articular</option>
                        <option value="CONDICION_NEUROLOGICA">Condición neurológica</option>
                    </select>
                </div>
                <div class="form-field">
                    <label>Ocupación / Perfil</label>
                    <select name="ocupacionPerfil">
                        <option value="">Seleccionar...</option>
                        <option value="SEDENTARIA_OFICINA">Sedentaria / Oficina</option>
                        <option value="DEPORTISTA_ALTO_RENDIMIENTO">Deportista de alto rendimiento</option>
                        <option value="AMATEUR_RECREATIVO">Amateur / Recreativo</option>
                        <option value="TRABAJO_FISICO_PESADO">Trabajo físico pesado</option>
                        <option value="ESTUDIANTE">Estudiante</option>
                        <option value="HOGAR">Hogar</option>
                        <option value="OTRO">Otro</option>
                    </select>
                </div>
                <div class="form-field full">
                    <label>Descripción del paciente</label>
                    <textarea rows="2" name="descripcionDetalladaPaciente" placeholder="Describe el motivo de la consulta con las palabras del paciente..."></textarea>
                </div>
                <div class="form-field full">
                    <label>Antecedentes patológicos</label>
                    <div class="checkbox-group">
                        <label><input type="checkbox" name="antecedentesPatologicos" value="HTA"> Hipertensión arterial</label>
                        <label><input type="checkbox" name="antecedentesPatologicos" value="DIABETES_MELLITUS"> Diabetes mellitus</label>
                        <label><input type="checkbox" name="antecedentesPatologicos" value="CIRUGIAS_PREVIAS"> Cirugías previas</label>
                        <label><input type="checkbox" name="antecedentesPatologicos" value="FRACTURAS_TRAUMATISMOS_PREVIOS"> Fracturas / Traumatismos previos</label>
                        <label><input type="checkbox" name="antecedentesPatologicos" value="ALERGIAS"> Alergias</label>
                        <label><input type="checkbox" name="antecedentesPatologicos" value="TRASTORNOS_COAGULACION_ANTICOAGULANTES"> Trastornos de coagulación / Anticoagulantes</label>
                        <label><input type="checkbox" name="antecedentesPatologicos" value="IMPLANTES_MARCAPASOS_PLACAS_METALICAS"> Implantes / Marcapasos / Placas metálicas</label>
                        <label><input type="checkbox" name="antecedentesPatologicos" value="EMBARAZO"> Embarazo</label>
                    </div>
                </div>
                <div class="form-field full">
                    <label>Otros antecedentes</label>
                    <input type="text" name="otrosAntecedentes" placeholder="Otros antecedentes relevantes no listados arriba">
                </div>
            </div>
        </div>

        <div class="internal-section">
            <h3>Valoración</h3>
            <div class="form-grid">
                <div class="form-field full">
                    <label>Tipo de dolor</label>
                    <div class="checkbox-group">
                        <label><input type="checkbox" name="tiposDolor" value="PUNZANTE"> Punzante</label>
                        <label><input type="checkbox" name="tiposDolor" value="OPRESIVO"> Opresivo</label>
                        <label><input type="checkbox" name="tiposDolor" value="QUEMANTE_ARDOR"> Quemante / Ardor</label>
                        <label><input type="checkbox" name="tiposDolor" value="SORDO_PROFUNDO"> Sordo / Profundo</label>
                        <label><input type="checkbox" name="tiposDolor" value="PULSATIL"> Pulsátil</label>
                        <label><input type="checkbox" name="tiposDolor" value="IRRADIADO"> Irradiado</label>
                        <label><input type="checkbox" name="tiposDolor" value="NEUROPATICO"> Neuropático</label>
                        <label><input type="checkbox" name="tiposDolor" value="LOCALIZADO"> Localizado</label>
                    </div>
                </div>
                <div class="form-field">
                    <label>Comportamiento del dolor</label>
                    <select name="comportamientoDolor">
                        <option value="">Seleccionar...</option>
                        <option value="CONTINUO">Continuo</option>
                        <option value="INTERMITENTE">Intermitente</option>
                        <option value="AUMENTA_CON_MOVIMIENTO">Aumenta con movimiento</option>
                        <option value="AUMENTA_EN_REPOSO">Aumenta en reposo</option>
                        <option value="NOCTURNO">Nocturno</option>
                    </select>
                </div>
                <div class="form-field">
                    <label>Intensidad del dolor (EVA): <output id="evaValue">0</output>/10</label>
                    <input type="range" name="intensidadDolor" min="0" max="10" value="0"
                        oninput="document.getElementById('evaValue').textContent = this.value">
                </div>
                <div class="form-field">
                    <label>Tensión sistólica (mmHg)</label>
                    <input type="number" name="tensionSistolica" placeholder="Ej: 120">
                </div>
                <div class="form-field">
                    <label>Tensión diastólica (mmHg)</label>
                    <input type="number" name="tensionDiastolica" placeholder="Ej: 80">
                </div>
                <div class="form-field">
                    <label>Frecuencia cardíaca (lpm)</label>
                    <input type="number" name="frecuenciaCardiaca" placeholder="Ej: 72">
                </div>
                <div class="form-field">
                    <label>Frecuencia respiratoria (rpm)</label>
                    <input type="number" name="frecuenciaRespiratoria" placeholder="Ej: 16">
                </div>
                <div class="form-field">
                    <label>Saturación de oxígeno (%)</label>
                    <input type="number" name="saturacionOxigeno" placeholder="Ej: 98">
                </div>
                <div class="form-field">
                    <label>Peso (kg)</label>
                    <input type="number" step="0.1" name="pesoKg" placeholder="Ej: 68.5">
                </div>
                <div class="form-field">
                    <label>Talla (cm)</label>
                    <input type="number" step="0.1" name="tallaCm" placeholder="Ej: 170">
                </div>
            </div>
        </div>

        <div class="internal-section">
            <h3>Objetivo y evolución</h3>
            <div class="form-grid">
                <div class="form-field full">
                    <label>Objetivo del tratamiento</label>
                    <textarea rows="3" name="objetivosTerapeuticos" placeholder="Ej: reducir dolor, recuperar movilidad..."></textarea>
                </div>
                <div class="form-field full">
                    <label>Evolución / observaciones</label>
                    <textarea rows="3" name="enfermedadActualEvolucion" placeholder="Notas de seguimiento entre sesiones..."></textarea>
                </div>
            </div>
        </div>
        </form>
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
            const idUsuario = btn.dataset.id;

            setTimeout(() => {
                const nombreSpan = document.getElementById("historyPacienteNombre");
                if (nombreSpan) nombreSpan.textContent = btn.dataset.nombre || "—";

                const documentoSpan = document.getElementById("historyPacienteDocumento");
                if (documentoSpan) documentoSpan.textContent = btn.dataset.documento || "—";

                const epsSpan = document.getElementById("historyPacienteEps");
                if (epsSpan) epsSpan.textContent = btn.dataset.eps || "—";

                const hiddenId = document.getElementById("historyIdUsuario");
                if (hiddenId) hiddenId.value = idUsuario || "";

                if (idUsuario) {
                    fetch(`/api/modal/historia-clinica/${idUsuario}`, { headers: getFetchHeaders() })
                        .then(response => response.ok ? response.json() : null)
                        .then(historia => {
                            const form = document.getElementById("clinicalHistoryForm");
                            if (!historia || !form) return;

                            const camposSimples = [
                                "motivoPrincipalConsulta", "ocupacionPerfil", "descripcionDetalladaPaciente",
                                "otrosAntecedentes", "comportamientoDolor", "intensidadDolor",
                                "tensionSistolica", "tensionDiastolica", "frecuenciaCardiaca",
                                "frecuenciaRespiratoria", "saturacionOxigeno", "pesoKg", "tallaCm",
                                "objetivosTerapeuticos", "enfermedadActualEvolucion"
                            ];

                            camposSimples.forEach(campo => {
                                const input = form.elements[campo];
                                if (input && historia[campo] !== undefined && historia[campo] !== null) {
                                    input.value = historia[campo];
                                    if (campo === "intensidadDolor") {
                                        const evaOutput = document.getElementById("evaValue");
                                        if (evaOutput) evaOutput.textContent = historia[campo];
                                    }
                                }
                            });

                            (historia.antecedentesPatologicos || []).forEach(valor => {
                                const cb = form.querySelector(`input[name="antecedentesPatologicos"][value="${valor}"]`);
                                if (cb) cb.checked = true;
                            });

                            (historia.tiposDolor || []).forEach(valor => {
                                const cb = form.querySelector(`input[name="tiposDolor"][value="${valor}"]`);
                                if (cb) cb.checked = true;
                            });
                        })
                        .catch(err => console.error("Error al cargar historia clínica:", err));
                }
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