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
@Table(name="registros")
public class RegistrationEntity implements Serializable {
    private static long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_registro")
    private Long idRegistro;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name="fecha_registro", nullable = false)
    private LocalDateTime fechaHora;

    @NotBlank
    @Size(max=60)
    @Column(name="origen", nullable = false, length = 60)
    private String origen;

    @NotBlank
    @Size(max=60)
    @Column(name="accion", nullable = false, length = 60)
    private String accion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_usuario", nullable = false)
    private UserEntity usuario;

    public RegistrationEntity(){

    }


}
