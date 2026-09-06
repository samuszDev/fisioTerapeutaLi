package com.usta.fisioterapiali.dto;

import lombok.Data;

import java.util.List;

/**
 * Respuesta de la pre-reserva. Se usa tanto para el 201 (creada)
 * como para el 409 (horario ocupado) y el 400 (datos inválidos).
 */
@Data
public class PreReservaResponse {

    private Boolean disponible;
    private Long reservaId;
    private String estado;

    private String nombrePaciente;
    private String servicio;
    private String sede;
    private Double valor;

    private String fechaInicio;
    private String fechaFin;
    private Integer duracionMinutos;

    /** Mensaje listo para que el bot se lo reenvíe al paciente. */
    private String mensaje;

    /** Solo en caso de conflicto: horarios libres el mismo día. */
    private List<String> alternativas;

    public static PreReservaResponse error(String mensaje) {
        PreReservaResponse r = new PreReservaResponse();
        r.setDisponible(false);
        r.setEstado("ERROR");
        r.setMensaje(mensaje);
        return r;
    }
}
