registerInternalViews({
    "new-client": {
        eyebrow: "NUEVO PACIENTE",
        title: "Crear cliente",
        content: `
            <form class="form-grid">
                <div class="form-field">
                    <label>Nombre completo</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>N° de documento</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>Tipo de documento</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>Fecha de nacimiento</label>
                    <input type="date">
                </div>
                <div class="form-field">
                    <label>Género</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field">
                    <label>EPS / Aseguradora</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>Ciudad de residencia</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>Ocupación / perfil</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>Teléfono / WhatsApp</label>
                    <input type="tel">
                </div>
                <div class="form-field">
                    <label>Correo</label>
                    <input type="email">
                </div>
                <div class="form-field">
                    <label>Contacto de emergencia</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>¿Quién lo refirió?</label>
                    <input type="text">
                </div>
            </form>
        `
    },

    "clinical-history": {
        eyebrow: "PERFIL 360",
        title: "Historia clínica",
        content: `
            <div class="clinical-top">
                <div class="info-placeholder">
                    <div></div>
                    <span></span>
                </div>
                <div class="info-placeholder">
                    <div></div>
                    <span></span>
                </div>
            </div>

            <div class="internal-section">
                <h3>Datos clínicos</h3>
                <div class="placeholder-line"></div>
                <div class="placeholder-line short"></div>
            </div>

            <div class="internal-section">
                <h3>Motivo principal</h3>
                <div class="placeholder-line"></div>
                <div class="placeholder-line short"></div>
            </div>

            <div class="internal-section">
                <h3>Valoración</h3>
                <div class="form-grid">
                    <div class="form-field">
                        <label>Tipo de dolor</label>
                        <input type="text">
                    </div>
                    <div class="form-field">
                        <label>Tensión arterial</label>
                        <input type="text">
                    </div>
                    <div class="form-field">
                        <label>Frecuencia cardíaca</label>
                        <input type="text">
                    </div>
                    <div class="form-field">
                        <label>Frecuencia respiratoria</label>
                        <input type="text">
                    </div>
                    <div class="form-field">
                        <label>IMC</label>
                        <input type="text">
                    </div>
                    <div class="form-field">
                        <label>EVA</label>
                        <input type="text">
                    </div>
                </div>
            </div>

            <div class="internal-section">
                <h3>Objetivo y evolución</h3>
                <div class="form-grid">
                    <div class="form-field full">
                        <label>Objetivo</label>
                        <textarea rows="3"></textarea>
                    </div>
                    <div class="form-field full">
                        <label>Evolución / observaciones</label>
                        <textarea rows="4"></textarea>
                    </div>
                </div>
            </div>
        `
    }

});