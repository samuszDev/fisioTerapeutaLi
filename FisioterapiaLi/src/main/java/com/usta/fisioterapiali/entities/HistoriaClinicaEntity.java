package com.usta.fisioterapiali.entities;

import com.usta.fisioterapiali.enums.*;
import com.usta.fisioterapiali.util.SignosVitalesCalculator;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "historia_clinica")
public class HistoriaClinicaEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historia_clinica")
    private Long idHistoriaClinica;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private UserEntity usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_principal_consulta", length = 50)
    private MotivoConsulta motivoPrincipalConsulta;

    @Column(name = "descripcion_detallada_paciente", columnDefinition = "TEXT")
    private String descripcionDetalladaPaciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "ocupacion_perfil", length = 40)
    private OcupacionPerfil ocupacionPerfil;

    @Size(max = 100)
    @Column(name = "ocupacion_otro", length = 100)
    private String ocupacionOtro;

    @ElementCollection(targetClass = AntecedentePatologico.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "historia_antecedentes", joinColumns = @JoinColumn(name = "id_historia_clinica"))
    @Enumerated(EnumType.STRING)
    @Column(name = "antecedente", length = 40)
    private Set<AntecedentePatologico> antecedentesPatologicos = new HashSet<>();

    @Column(name = "otros_antecedentes", columnDefinition = "TEXT")
    private String otrosAntecedentes;

    @Min(40) @Max(300)
    @Column(name = "tension_sistolica")
    private Integer tensionSistolica;

    @Min(20) @Max(200)
    @Column(name = "tension_diastolica")
    private Integer tensionDiastolica;

    @Min(20) @Max(250)
    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    @Min(5) @Max(60)
    @Column(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria;

    @Min(0) @Max(100)
    @Column(name = "saturacion_oxigeno")
    private Integer saturacionOxigeno;

    @DecimalMin("1.0")
    @Column(name = "peso_kg")
    private Double pesoKg;

    @DecimalMin("30.0")
    @Column(name = "talla_cm")
    private Double tallaCm;

    @Min(0) @Max(10)
    @Column(name = "intensidad_dolor")
    private Integer intensidadDolor;

    @ElementCollection(targetClass = TipoDolor.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "historia_tipo_dolor", joinColumns = @JoinColumn(name = "id_historia_clinica"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dolor", length = 30)
    private Set<TipoDolor> tiposDolor = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "comportamiento_dolor", length = 30)
    private ComportamientoDolor comportamientoDolor;

    @Column(name = "objetivos_terapeuticos", columnDefinition = "TEXT")
    private String objetivosTerapeuticos;

    @Column(name = "enfermedad_actual_evolucion", columnDefinition = "TEXT")
    private String enfermedadActualEvolucion;

    @Transient
    public EstadoTensionArterial getEstadoTensionArterial() {
        return SignosVitalesCalculator.calcularEstadoTensionArterial(tensionSistolica, tensionDiastolica);
    }

    @Transient
    public EstadoFrecuenciaCardiaca getEstadoFrecuenciaCardiaca() {
        return SignosVitalesCalculator.calcularEstadoFrecuenciaCardiaca(frecuenciaCardiaca);
    }

    @Transient
    public EstadoFrecuenciaRespiratoria getEstadoFrecuenciaRespiratoria() {
        return SignosVitalesCalculator.calcularEstadoFrecuenciaRespiratoria(frecuenciaRespiratoria);
    }

    @Transient
    public EstadoSaturacionOxigeno getEstadoSaturacionOxigeno() {
        return SignosVitalesCalculator.calcularEstadoSaturacionOxigeno(saturacionOxigeno);
    }

    @Transient
    public Double getImc() {
        return SignosVitalesCalculator.calcularIMC(pesoKg, tallaCm);
    }

    @Transient
    public EstadoIMC getEstadoImc() {
        return SignosVitalesCalculator.calcularEstadoIMC(getImc());
    }

    @Transient
    public NivelDolorEva getNivelDolorEva() {
        return SignosVitalesCalculator.calcularNivelDolorEva(intensidadDolor);
    }

    public HistoriaClinicaEntity() {
    }
}