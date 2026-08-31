package com.usta.fisioterapiali.entities;

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

    @NotBlank
    @Size(max = 20)
    @Column(name="google_calendar_id", nullable = false, length = 20)
    private String googleCalendarId;

    @Column(name="estado_reserva", nullable = false)
    private boolean estadoR = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_usuario", nullable = false)
    private UserEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_servicio",nullable = false)
    private ServiceEntity Servicio;

}
