package com.usta.fisioterapiali.entities;

import com.usta.fisioterapiali.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name="usuario")
public class UserEntity implements Serializable {
    private static long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Long idUsuario;

    @NotBlank
    @Size(max=60)
    @Column(name="nombre", nullable = false, length = 60)
    private String nombre;

    @NotBlank
    @Size(max=60)
    @Column(name="clave", nullable = false, length = 60)
    private String clave;

    @NotBlank
    @Size(max=60)
    @Column(name="user_telegram", nullable = true, length = 60)
    private String telegramUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private TipoUsuario rol;

    @NotBlank
    @Email
    @Size(max=100)
    @Column(name="email", nullable = false, length = 100)
    private String email;


    @Size(max=20)
    @Column(name="documento", length = 20)
    private String documento;

    @Size(max=20)
    @Column(name="telefono", length = 20)
    private String telefono;

    @Column(name="fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Size(max=20)
    @Column(name="genero", length = 20)
    private String genero;

    @Size(max=150)
    @Column(name="contacto_emergencia", length = 150)
    private String contactoEmergencia;

    @Size(max=60)
    @Column(name="referido_por", length = 60)
    private String referidoPor;

    @Min(0)
    @Max(5)
    @Column(name="contador_referidos", columnDefinition = "integer default 0")
    private Integer contadorReferidos = 0;

    @NotBlank
    @Size(max=60)
    @Column(name="eps", nullable = true, length = 60)
    private String eps;

    @Column(name = "acepta_datos")
    private Boolean aceptaDatos = false;

    @Column(name = "fecha_aceptacion")
    private LocalDateTime fechaAceptacion;

    public UserEntity(){

    }


}
