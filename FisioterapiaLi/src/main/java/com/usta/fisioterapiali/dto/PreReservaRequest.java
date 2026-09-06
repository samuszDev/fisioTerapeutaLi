package com.usta.fisioterapiali.dto;

import lombok.Data;

/**
 * Datos que envía n8n (bot de Telegram) o la web para solicitar una pre-reserva.
 * El backend calcula por su cuenta: fecha de fin, sede, valor y disponibilidad.
 */
@Data
public class PreReservaRequest {

    private String nombrePaciente;
    private String telefono;
    private String correo;
    private String documento;

    /** Nombre aproximado del servicio, ej. "terapia física". */
    private String servicio;

    /** ISO 8601, ej. "2026-09-11T16:00:00". Se ignora la zona horaria si viene. */
    private String fechaInicio;

    /** "telegram" o "web". Solo para trazabilidad. */
    private String canal;

    /** Chat id de Telegram, si aplica. */
    private String telegramUserId;
}
