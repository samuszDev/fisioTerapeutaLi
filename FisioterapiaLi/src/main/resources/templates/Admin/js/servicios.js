registerInternalViews({
    "new-service": {
        eyebrow: "CATÁLOGO",
        title: "Nuevo servicio",
        content: `
            <form class="form-grid">
                <div class="form-field">
                    <label>Nombre del servicio</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>Precio</label>
                    <input type="number">
                </div>
                <div class="form-field">
                    <label>Duración</label>
                    <input type="text">
                </div>
                <div class="form-field">
                    <label>Estado</label>
                    <select><option>Seleccionar...</option></select>
                </div>
                <div class="form-field full">
                    <label>Descripción</label>
                    <textarea rows="4"></textarea>
                </div>
            </form>
        `
    }

});