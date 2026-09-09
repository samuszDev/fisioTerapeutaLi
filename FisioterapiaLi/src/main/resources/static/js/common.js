const pages = document.querySelectorAll(".page");
const navItems = document.querySelectorAll(".nav-item");
const sidebar = document.getElementById("sidebar");
const menuButton = document.getElementById("menuButton");

const modalOverlay = document.getElementById("modalOverlay");
const modalTitle = document.getElementById("modalTitle");
const modalEyebrow = document.getElementById("modalEyebrow");
const modalContent = document.getElementById("modalContent");
const closeModalButton = document.getElementById("closeModal");
const modalCancel = document.getElementById("modalCancel");
const modalFinish = document.getElementById("modalFinish");

let internalViews = {};

function registerInternalViews(views) {
    internalViews = Object.assign(internalViews, views);
}

function showPage(pageId) {
    pages.forEach(page => {
        page.classList.toggle("active", page.id === pageId);
    });

    navItems.forEach(item => {
        item.classList.toggle("active", item.dataset.page === pageId);
    });

    if (sidebar) {
        sidebar.classList.remove("open");
    }

    window.scrollTo({ top: 0, behavior: "smooth" });
}

navItems.forEach(item => {
    item.addEventListener("click", () => {
        const destination = item.dataset.href;

        if (destination) {
            window.location.href = destination;
            return;
        }

        showPage(item.dataset.page);
    });
});

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

// FUNCIÓN GLOBAL DE NOTIFICACIONES PERSONALIZADAS (TOAST)
function mostrarNotificacionPersonalizada(mensaje, tipo = "error") {
    const alertaAntigua = document.getElementById("customToastAlert");
    if (alertaAntigua) alertaAntigua.remove();

    const toast = document.createElement("div");
    toast.id = "customToastAlert";
    toast.style.cssText = `
        position: fixed;
        bottom: 30px;
        right: 30px;
        background: #1e293b;
        color: #ffffff;
        padding: 16px 22px;
        border-radius: 10px;
        box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        display: flex;
        align-items: center;
        gap: 14px;
        z-index: 10000;
        font-family: 'Poppins', sans-serif;
        font-size: 14px;
        border-left: 5px solid ${tipo === 'error' ? '#d9534f' : '#c5a880'};
        max-width: 420px;
    `;

    toast.innerHTML = `
        <div style="font-size: 20px;">${tipo === 'error' ? '⚠️' : 'ℹ️'}</div>
        <div style="flex: 1; line-height: 1.4;">${mensaje}</div>
        <button onclick="this.parentElement.remove()" style="background: transparent; border: none; color: #94a3b8; font-size: 18px; cursor: pointer; padding: 0 4px;">&times;</button>
    `;

    document.body.appendChild(toast);

    setTimeout(() => {
        if (toast) toast.remove();
    }, 6000);
}

function openInternalView(viewName) {
    const view = internalViews[viewName];
    if (!view || !modalOverlay) return;

    modalEyebrow.textContent = view.eyebrow;
    modalTitle.textContent = view.title;
    modalContent.innerHTML = view.content;

    // 1. CREAR CLIENTE
    if (viewName === "new-client") {
        const finishBtn = document.getElementById("modalFinish");

        if (finishBtn) {
            const newFinishBtn = finishBtn.cloneNode(true);
            finishBtn.parentNode.replaceChild(newFinishBtn, finishBtn);

            newFinishBtn.addEventListener("click", () => {
                const form = document.getElementById("newClientForm");
                if (!form) return;

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                const formData = new FormData(form);
                const data = Object.fromEntries(formData.entries());

                fetch("/api/modal/pacientes", {
                    method: "POST",
                    headers: getFetchHeaders(),
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            mostrarNotificacionPersonalizada("¡Paciente guardado correctamente!", "success");
                            closeInternalView();
                            if (typeof cargarClientes === "function") cargarClientes();
                        } else {
                            const msg = await response.text();
                            mostrarNotificacionPersonalizada("Error del servidor: " + msg, "error");
                        }
                    })
                    .catch(error => {
                        console.error("Error de red:", error);
                        mostrarNotificacionPersonalizada("Error al intentar conectar con el backend.", "error");
                    });
            });
        }
    }

    // 2. EDITAR CLIENTE
    if (viewName === "edit-client") {
        const finishBtn = document.getElementById("modalFinish");
        if (finishBtn) {
            const newFinishBtn = finishBtn.cloneNode(true);
            finishBtn.parentNode.replaceChild(newFinishBtn, finishBtn);

            newFinishBtn.addEventListener("click", () => {
                const form = document.getElementById("editClientForm");
                if (!form) return;

                const idUsuario = document.getElementById("editIdUsuario").value;
                const formData = new FormData(form);
                const data = Object.fromEntries(formData.entries());

                fetch(`/api/modal/pacientes/${idUsuario}`, {
                    method: "PUT",
                    headers: getFetchHeaders(),
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            mostrarNotificacionPersonalizada("¡Cliente actualizado correctamente!", "success");
                            closeInternalView();
                            if (typeof cargarClientes === "function") cargarClientes();
                        } else {
                            const errorMsg = await response.text();
                            mostrarNotificacionPersonalizada("Error: " + errorMsg, "error");
                        }
                    })
                    .catch(err => {
                        console.error("Error en petición PUT:", err);
                        mostrarNotificacionPersonalizada("Error al conectar con el servidor.", "error");
                    });
            });
        }
    }

    // 3. NUEVA CITA (AGENDA)
    if (viewName === "new-appointment") {
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
                            mostrarNotificacionPersonalizada("¡Cita agendada correctamente!", "success");
                            closeInternalView();
                            if (typeof cargarCitas === "function") cargarCitas();
                        } else {
                            const errorMsg = await response.text();
                            mostrarNotificacionPersonalizada("Error al agendar la cita: " + errorMsg, "error");
                        }
                    })
                    .catch(error => {
                        console.error("Error de red:", error);
                        mostrarNotificacionPersonalizada("Error al intentar conectar con el servidor.", "error");
                    });
            });
        }
    }

    // 4. EDITAR CITA (AGENDA)
    if (viewName === "edit-appointment") {
        const finishBtn = document.getElementById("modalFinish");

        if (finishBtn) {
            const newFinishBtn = finishBtn.cloneNode(true);
            finishBtn.parentNode.replaceChild(newFinishBtn, finishBtn);

            newFinishBtn.addEventListener("click", () => {
                const form = document.getElementById("editAppointmentForm");
                if (!form) return;

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                const formData = new FormData(form);
                const data = Object.fromEntries(formData.entries());
                const idReserva = data.idReserva;

                if (!idReserva) {
                    mostrarNotificacionPersonalizada("Error: No se encontró el ID de la reserva a modificar.", "error");
                    return;
                }

                fetch(`/api/modal/citas/${idReserva}`, {
                    method: "PUT",
                    headers: getFetchHeaders(),
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            mostrarNotificacionPersonalizada("¡Cita actualizada correctamente!", "success");
                            closeInternalView();
                            if (typeof cargarCitas === "function") cargarCitas();
                        } else {
                            const errorMsg = await response.text();
                            mostrarNotificacionPersonalizada("Error al actualizar la cita: " + errorMsg, "error");
                        }
                    })
                    .catch(error => {
                        console.error("Error de red:", error);
                        mostrarNotificacionPersonalizada("Error al intentar conectar con el servidor.", "error");
                    });
            });
        }
    }

    // 5. NUEVO SERVICIO
    if (viewName === "new-service") {
        const finishBtn = document.getElementById("modalFinish");

        if (finishBtn) {
            const newFinishBtn = finishBtn.cloneNode(true);
            finishBtn.parentNode.replaceChild(newFinishBtn, finishBtn);

            newFinishBtn.addEventListener("click", () => {
                const form = document.getElementById("newServiceForm");
                if (!form) return;

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                const formData = new FormData(form);
                const rawData = Object.fromEntries(formData.entries());

                const data = {
                    nombreS: rawData.nombreS || rawData.nombreService,
                    precioIndividual: Number(rawData.precioIndividual || rawData.precio),
                    duracionS: rawData.duracionS || rawData.duracion,
                    estadoS: rawData.estadoS === "true" || rawData.estado === "ACTIVO"
                };

                fetch("/api/modal/servicios", {
                    method: "POST",
                    headers: getFetchHeaders(),
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            mostrarNotificacionPersonalizada("¡Servicio creado correctamente!", "success");
                            closeInternalView();
                            // Recargar solo la sección disparando un evento de DOMContentLoaded simulado o función local
                            location.reload(); // Solo si fuera necesario, pero de preferencia se recarga la grilla si existe función global
                        } else {
                            const errorMsg = await response.text();
                            mostrarNotificacionPersonalizada("Error del servidor: " + errorMsg, "error");
                        }
                    })
                    .catch(error => {
                        console.error("Error de red:", error);
                        mostrarNotificacionPersonalizada("Error al intentar conectar con el backend.", "error");
                    });
            });
        }
    }

    // 6. EDITAR SERVICIO
    if (viewName === "edit-service") {
        const finishBtn = document.getElementById("modalFinish");

        if (finishBtn) {
            const newFinishBtn = finishBtn.cloneNode(true);
            finishBtn.parentNode.replaceChild(newFinishBtn, finishBtn);

            newFinishBtn.addEventListener("click", () => {
                const form = document.getElementById("editServiceForm");
                if (!form) return;

                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                const idServicio = document.getElementById("editIdServicio").value;
                const formData = new FormData(form);
                const rawData = Object.fromEntries(formData.entries());

                if (!idServicio) {
                    mostrarNotificacionPersonalizada("Error: No se encontró el ID del servicio a modificar.", "error");
                    return;
                }

                const data = {
                    nombreS: rawData.nombreS,
                    precioIndividual: Number(rawData.precioIndividual),
                    duracionS: rawData.duracionS,
                    estadoS: rawData.estadoS === "true"
                };

                fetch(`/api/modal/servicios/${idServicio}`, {
                    method: "PUT",
                    headers: getFetchHeaders(),
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            mostrarNotificacionPersonalizada("¡Servicio actualizado correctamente!", "success");
                            closeInternalView();
                        } else {
                            const errorMsg = await response.text();
                            mostrarNotificacionPersonalizada("Error al actualizar el servicio: " + errorMsg, "error");
                        }
                    })
                    .catch(error => {
                        console.error("Error de red:", error);
                        mostrarNotificacionPersonalizada("Error al intentar conectar con el servidor.", "error");
                    });
            });
        }
    }

    modalOverlay.classList.add("open");
    document.body.style.overflow = "hidden";
}

function closeInternalView() {
    if (!modalOverlay) return;

    modalOverlay.classList.remove("open");
    document.body.style.overflow = "";

    if (modalContent) {
        modalContent.innerHTML = "";
    }
}

document.addEventListener("click", event => {
    const trigger = event.target.closest("[data-open-view]");

    if (trigger) {
        openInternalView(trigger.dataset.openView);
    }
});

if (closeModalButton) {
    closeModalButton.addEventListener("click", closeInternalView);
}

if (modalCancel) {
    modalCancel.addEventListener("click", closeInternalView);
}

if (modalFinish) {
    modalFinish.addEventListener("click", closeInternalView);
}

if (modalOverlay) {
    modalOverlay.addEventListener("click", event => {
        if (event.target === modalOverlay) {
            closeInternalView();
        }
    });
}

document.addEventListener("keydown", event => {
    if (
        event.key === "Escape" &&
        modalOverlay &&
        modalOverlay.classList.contains("open")
    ) {
        closeInternalView();
    }
});

if (menuButton && sidebar) {
    menuButton.addEventListener("click", () => {
        sidebar.classList.toggle("open");
    });
}

// Reemplazo del confirm() nativo del navegador por un modal con el estilo de la marca
function showConfirmDialog(message, acceptLabel = "Sí, eliminar") {
    return new Promise(resolve => {
        const overlay = document.createElement("div");
        overlay.className = "confirm-overlay";
        overlay.innerHTML = `
            <div class="confirm-box">
                <div class="confirm-icon">!</div>
                <p class="confirm-message">${message}</p>
                <div class="confirm-actions">
                    <button class="btn btn-outline confirm-cancel">Cancelar</button>
                    <button class="btn btn-gold confirm-accept">${acceptLabel}</button>
                </div>
            </div>
        `;
        document.body.appendChild(overlay);
        document.body.style.overflow = "hidden";

        function onKey(e) {
            if (e.key === "Escape") close(false);
        }
        document.addEventListener("keydown", onKey);

        function close(result) {
            overlay.remove();
            document.body.style.overflow = "";
            document.removeEventListener("keydown", onKey);
            resolve(result);
        }

        overlay.querySelector(".confirm-cancel").addEventListener("click", () => close(false));
        overlay.querySelector(".confirm-accept").addEventListener("click", () => close(true));
        overlay.addEventListener("click", e => {
            if (e.target === overlay) close(false);
        });
    });
}