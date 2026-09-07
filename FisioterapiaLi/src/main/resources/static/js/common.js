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
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        if (response.ok) {
                            alert("¡Paciente guardado correctamente!");
                            closeInternalView();
                            window.location.reload();
                        } else {
                            response.text().then(msg => alert("Error del servidor: " + msg));
                        }
                    })
                    .catch(error => alert("Error al intentar conectar con el backend."));
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
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            alert("¡Cliente actualizado correctamente!");
                            closeInternalView();
                            window.location.reload();
                        } else {
                            const errorMsg = await response.text();
                            alert("Error: " + errorMsg);
                        }
                    })
                    .catch(err => console.error("Error en petición PUT:", err));
            });
        }
    }

    // 3. NUEVA CITA / RESERVA
    if (viewName === "new-appointment") {
        fetch("/api/modal/pacientes")
            .then(res => res.json())
            .then(pacientes => {
                const selectP = document.getElementById("selectPaciente");
                if (!selectP) return;

                const clientes = pacientes.filter(p => p.rol === "CLIENTE");
                selectP.innerHTML = '<option value="">Seleccionar paciente...</option>';
                clientes.forEach(p => {
                    selectP.innerHTML += `<option value="${p.idUsuario}">${p.nombre} (${p.documento || 'S/D'})</option>`;
                });
            }).catch(err => console.error("Error al cargar pacientes", err));

        fetch("/api/modal/servicios")
            .then(res => res.json())
            .then(servicios => {
                const selectS = document.getElementById("selectServicio");
                if (!selectS) return;

                selectS.innerHTML = '<option value="">Seleccionar servicio...</option>';
                servicios.forEach(s => {
                    const nombreReal = s.nombreS || s.nombre || 'Servicio sin nombre';
                    const idReal = s.idServicio || s.id || '';
                    selectS.innerHTML += `<option value="${idReal}">${nombreReal}</option>`;
                });
            }).catch(err => console.error("Error al cargar servicios", err));

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
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            alert("¡Cita agendada correctamente!");
                            closeInternalView();
                            window.location.reload();
                        } else {
                            const errorMsg = await response.text();
                            alert("Error del servidor: " + errorMsg);
                        }
                    })
                    .catch(error => {
                        console.error("Error de red:", error);
                        alert("Error al conectar con el backend.");
                    });
            });
        }
    }

    // 4. NUEVO SERVICIO
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
                const data = Object.fromEntries(formData.entries());

                fetch("/api/modal/servicios", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(data)
                })
                    .then(async response => {
                        if (response.ok) {
                            alert("¡Servicio creado correctamente!");
                            closeInternalView();
                            window.location.reload();
                        } else {
                            const errorMsg = await response.text();
                            alert("Error del servidor: " + errorMsg);
                        }
                    })
                    .catch(error => {
                        console.error("Error de red:", error);
                        alert("Error al intentar conectar con el backend.");
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