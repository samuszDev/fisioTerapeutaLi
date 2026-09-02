package com.usta.fisioterapiali.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalTime;

@Data
@Entity
@Table(name="Horarios")

public class ScheduleEntity implements Serializable {
    private static long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_horario")
    private Long idHorario;

    @NotNull
    @Positive
    @Column(name="dia_semana", nullable = false)
    private Integer diaSemana;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name="hora_apertura", nullable = false)
    private LocalTime horaApertura;

    @NotNull
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name="hora_cierre", nullable = false)
    private LocalTime horaCierre;

    @NotNull
    @Positive
    @Column(name="tiempo_anticipacion", nullable = false)
    private Integer tiempoAnticipado;

    @NotBlank
    @Size(max=50)
    @Column(name="sede", nullable = false, length = 50)
    private String sede;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name="hora_inicio_descanso")
    private LocalTime horaInicioDescanso;

    @DateTimeFormat(pattern = "HH:mm")
    @Column(name="hora_fin_descanso")
    private LocalTime horaFinDescanso;

    public ScheduleEntity() {

    }



}
