
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

    if (viewName === "new-appointment") {
        const appointmentDate = document.getElementById("appointmentDate");

        if (appointmentDate) {
            const today = new Date();
            const localDate = [
                today.getFullYear(),
                String(today.getMonth() + 1).padStart(2, "0"),
                String(today.getDate()).padStart(2, "0")
            ].join("-");

            appointmentDate.min = localDate;
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