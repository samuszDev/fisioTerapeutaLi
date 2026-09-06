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

    // Configuración específica si se abre la modal de crear cliente
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

                console.log("Enviando datos al servidor:", data);

                fetch("/api/modal/pacientes", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(data)
                })
                    .then(response => {
                        console.log("Respuesta del servidor (Status):", response.status);
                        if (response.ok) {
                            alert("¡Paciente guardado correctamente!");
                            closeInternalView();
                            window.location.reload();
                        } else {
                            response.text().then(msg => alert("Error del servidor: " + msg));
                        }
                    })
                    .catch(error => {
                        console.error("Error de red/petición:", error);
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