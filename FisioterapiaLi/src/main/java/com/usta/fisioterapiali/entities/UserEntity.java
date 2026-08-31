package com.usta.fisioterapiali.entities;

import com.usta.fisioterapiali.enums.TipoUsuario;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

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
    @Column(name="user_telegram", nullable = false, length = 60)
    private String telegramUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private TipoUsuario rol;

    @NotBlank
    @Email
    @Size(max=100)
    @Column(name="email", nullable = false, length = 100)
    private String email;


    public UserEntity(){

    }




}
