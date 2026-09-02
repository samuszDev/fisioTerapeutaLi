package com.usta.fisioterapiali.entities;

import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name="Servicios")
public class ServiceEntity implements Serializable {
    private static long serialVersionUID=1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_servicio")
    private Long idServicio;

    @NotBlank
    @Size(max=60)
    @Column(name="nombre_servicio", nullable = false, length = 60)
    private String nombreS;

    @NotNull
    @Positive
    @Column(name="duracion_min", nullable = false)
    private Integer duracionS;

    @Column(name="estado_servicio", nullable = false)
    private boolean estadoS = true;

    @NotNull
    @Positive
    @Column(name="precio_individual", nullable = false)
    private Double precioIndividual;

    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanEntity> planes;

    public ServiceEntity(){

    }

}
