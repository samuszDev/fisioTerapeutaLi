package com.usta.fisioterapiali.entities;

import jakarta.persistence.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="resenas")
public class ReviewEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_resena")
    private Long idResena;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name="calificacion", nullable = false)
    private Integer calificacion;

    @Size(max = 500)
    @Column(name="comentario", length = 500)
    private String comentario;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name="fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_usuario", nullable = false)
    private UserEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_servicio", nullable = false)
    private ServiceEntity servicio;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_cita", nullable = false, unique = true)
    private ReservationEntity cita;

    public ReviewEntity() {
    }
}