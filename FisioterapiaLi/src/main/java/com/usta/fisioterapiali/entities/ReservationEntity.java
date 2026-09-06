package com.usta.fisioterapiali.entities;

import com.usta.fisioterapiali.enums.MetodoPago;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="Reservas")

public class ReservationEntity implements Serializable {
    private static long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_reserva")
    private Long idReserva;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name="fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name="fecha_hora_fin", nullable = false)
    private LocalDateTime fechaFin;

    // El evento de Calendar se crea DESPUES de guardar la reserva,
    // asi que empieza en null y n8n lo completa con PATCH.
    // Los IDs de Google superan ampliamente los 20 caracteres.
    @Size(max = 120)
    @Column(name="google_calendar_id", nullable = true, length = 120)
    private String googleCalendarId;

    @Column(name="estado_reserva", nullable = false)
    private boolean estadoR = true;

    @NotBlank
    @Size(max = 50)
    @Column(name="sede", nullable = false, length = 50)
    private String sede;


    @Column(name="pago_confirmado", nullable = false)
    private Boolean pagoConfirmado = false;

    @Enumerated(EnumType.STRING)
    @Column(name="metodo_pago", length = 30)
    private MetodoPago metodoPago;

    @Size(max = 30)
    @Column(name="estado_cita", nullable = false, length = 30)
    private String estadoCita = "PENDIENTE";


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_usuario", nullable = false)
    private UserEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_servicio", nullable = false)
    private ServiceEntity servicio;

    public ReservationEntity() {

    }


}