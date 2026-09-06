// Asegúrate de que common.js se carga antes en tu HTML para que registerInternalViews esté disponible
if (typeof registerInternalViews === "function") {
    registerInternalViews({
        "new-appointment": {
            eyebrow: "NUEVA RESERVA",
            title: "Agendar cita",
            content: `
                <form class="form-grid" id="newAppointmentForm">
                    <div class="form-field">
    <label>Paciente</label>
    <select id="pacienteSelect" required><option value="">Seleccionar paciente...</option></select>
</div>
<div class="form-field">
    <label>Servicio</label>
    <select id="servicioSelect" required><option value="">Seleccionar servicio...</option></select>
</div>
                    <div class="form-field">
                        <label>Fecha</label>
                        <input type="date" id="appointmentDate" required>
                    </div>
                    <div class="form-field">
                        <label>Hora</label>
                        <input type="time" required>
                    </div>
                    <div class="form-field">
                        <label>Sede</label>
                        <select required>
                            <option value="">Seleccionar sede...</option>
                            <option>Sede Tunja (Lunes a Viernes)</option>
                            <option>Sede Turmequé (Sábados y Domingos)</option>
                        </select>
                    </div>
                    <div class="form-field">
                        <label>Estado de pago</label>
                        <select required>
                            <option value="anticipado">Pago anticipado 100%</option>
                        </select>
                    </div>
                    <div class="form-field full">
                        <label>Observaciones</label>
                        <textarea rows="4" placeholder="Notas clínicas o detalles de la reserva..."></textarea>
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
}