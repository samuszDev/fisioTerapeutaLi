// ELIMINAR CITA
document.addEventListener("click", event => {
    if (event.target.classList.contains("btn-delete-cita")) {
        const idCita = event.target.dataset.id;

        const confirmFunc = typeof showConfirmDialog === "function"
            ? showConfirmDialog("¿Estás seguro de que deseas eliminar esta cita?")
            : Promise.resolve(confirm("¿Estás seguro de que deseas eliminar esta cita?"));

        confirmFunc.then(confirmado => {
            if (!confirmado) return;

            fetch(`/api/modal/citas/${idCita}`, { method: "DELETE" })
                .then(async res => {
                    if (res.ok) {
                        if (typeof mostrarNotificacionPersonalizada === "function") {
                            mostrarNotificacionPersonalizada("Cita eliminada correctamente", "success");
                        } else {
                            alert("Cita eliminada correctamente");
                        }
                        cargarCitas();
                    } else {
                        const msg = await res.text();
                        if (typeof mostrarNotificacionPersonalizada === "function") {
                            mostrarNotificacionPersonalizada("Error: " + msg, "error");
                        } else {
                            alert("Error: " + msg);
                        }
                    }
                })
                .catch(err => {
                    console.error("Error de red al eliminar:", err);
                    if (typeof mostrarNotificacionPersonalizada === "function") {
                        mostrarNotificacionPersonalizada("Error de red al intentar eliminar la cita.", "error");
                    } else {
                        alert("Error de red al intentar eliminar la cita.");
                    }
                });
        });
    }
});