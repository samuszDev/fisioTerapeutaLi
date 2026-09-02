package com.usta.fisioterapiali.entities;

import jakarta.persistence.*;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Data
@Entity
@Table(name="planes")
public class PlanEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_plan")
    private Long idPlan;

    @NotBlank
    @Column(name="nombre_plan", nullable = false, length = 100)
    private String nombrePlan; // Ej: "Paquete de 10 sesiones", "Grupal (4 personas)"

    @NotNull
    @Positive
    @Column(name="cantidad_sesiones", nullable = false)
    private Integer cantidadSesiones; // Ej: 5, 10, 8, 12

    @NotNull
    @Positive
    @Column(name="precio_total", nullable = false)
    private Double precioTotal; // Ej: 850000.0

    @Column(name="es_grupal", nullable = false)
    private Boolean esGrupal = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="id_servicio", nullable = false)
    private ServiceEntity servicio;

    public PlanEntity() {}
}