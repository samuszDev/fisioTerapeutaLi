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

    "reports": {
        eyebrow: "VISIÓN ADMINISTRATIVA",
        title: "Reportes",
        content: `
            <div class="internal-section">
                <h3>Indicadores</h3>
                <div class="placeholder-line"></div>
                <div class="placeholder-line"></div>
                <div class="placeholder-line short"></div>
            </div>

            <div class="internal-section">
                <h3>Resumen</h3>
                <div class="placeholder-line"></div>
                <div class="placeholder-line short"></div>
            </div>
        `
    }

});