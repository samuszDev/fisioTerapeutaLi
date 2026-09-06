registerInternalViews({
    "new-appointment": {
        eyebrow: "NUEVA RESERVA",
        title: "Agendar cita",
        content: `
            <form class="form-grid" id="newAppointmentForm">
                <div class="form-field">
                    <label>Paciente</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>Servicio</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>Fecha</label>
                    <input type="date" id="appointmentDate">
                </div>
                <div class="form-field">
                    <label>Hora</label>
                    <input type="time">
                </div>
                <div class="form-field">
                    <label>Sede</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>Estado de pago</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field full">
                    <label>Observaciones</label>
                    <textarea rows="4" placeholder=""></textarea>
                </div>
            </form>
        `
    },

    "edit-appointment": {
        eyebrow: "EDICIÓN DE RESERVA",
        title: "Modificar cita",
        content: `
            <form class="form-grid">
                <div class="form-field">
                    <label>Paciente</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>Servicio</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>Sede</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>Nueva fecha</label>
                    <input type="date">
                </div>
                <div class="form-field">
                    <label>Nueva hora</label>
                    <input type="time">
                </div>
                <div class="form-field">
                    <label>Estado</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>Pago</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field full">
                    <label>Motivo del cambio</label>
                    <textarea rows="3"></textarea>
                </div>
            </form>
        `
    }

});