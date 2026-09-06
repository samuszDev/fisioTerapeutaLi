package com.usta.fisioterapiali.entities;

import com.usta.fisioterapiali.enums.*;
import com.usta.fisioterapiali.util.SignosVitalesCalculator;
import jakarta.persistence.*;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
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

    // Relacion con el paciente. Los datos basicos (nombre, documento,
    // fecha de nacimiento, genero, telefono, email, contacto emergencia,
    // referido por y contador de referidos) ya viven en UserEntity.
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false, unique = true)
    private UserEntity usuario;

    // ==========================================================
    // MODULO 1: FICHA DEL PACIENTE (INGRESO)
    // ==========================================================

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "eps_aseguradora", nullable = false, length = 30)
    private EpsAseguradora epsAseguradora;

    @Size(max = 100)
    @Column(name = "eps_otro", length = 100)
    private String epsOtro; // Solo si epsAseguradora = OTRO

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ciudad_residencia", nullable = false, length = 30)
    private CiudadResidencia ciudadResidencia;

    @Size(max = 100)
    @Column(name = "ciudad_otro", length = 100)
    private String ciudadOtro; // Solo si ciudadResidencia = OTRO

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ocupacion_perfil", nullable = false, length = 40)
    private OcupacionPerfil ocupacionPerfil;

    @Size(max = 100)
    @Column(name = "ocupacion_otro", length = 100)
    private String ocupacionOtro; // Solo si ocupacionPerfil = OTRO

    // Nombre, parentesco y telefono del contacto de emergencia.
    // (UserEntity.contactoEmergencia guarda el resumen; aqui se detalla)
    @Size(max = 100)
    @Column(name = "contacto_emergencia_nombre", length = 100)
    private String contactoEmergenciaNombre;

    @Size(max = 60)
    @Column(name = "contacto_emergencia_parentesco", length = 60)
    private String contactoEmergenciaParentesco;

    @Size(max = 20)
    @Column(name = "contacto_emergencia_telefono", length = 20)
    private String contactoEmergenciaTelefono;

    // ==========================================================
    // MODULO 2: MOTIVO DE CONSULTA Y ANAMNESIS
    // ==========================================================

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "motivo_principal_consulta", nullable = false, length = 50)
    private MotivoConsulta motivoPrincipalConsulta;

    @Column(name = "descripcion_detallada_paciente", columnDefinition = "TEXT")
    private String descripcionDetalladaPaciente;

    @Column(name = "enfermedad_actual_evolucion", columnDefinition = "TEXT")
    private String enfermedadActualEvolucion;

    // Checkboxes de seleccion multiple -> tabla aparte (historia_antecedentes)
    @ElementCollection(targetClass = AntecedentePatologico.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "historia_antecedentes",
            joinColumns = @JoinColumn(name = "id_historia_clinica")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "antecedente", length = 40)
    private Set<AntecedentePatologico> antecedentesPatologicos = new HashSet<>();

    @Column(name = "otros_antecedentes", columnDefinition = "TEXT")
    private String otrosAntecedentes;

    // ==========================================================
    // MODULO 3: SIGNOS VITALES Y ANTROPOMETRIA
    // (Solo se guardan los valores crudos; el estado/semaforo
    //  se calcula en caliente con los metodos @Transient de abajo)
    // ==========================================================

    @Min(40)
    @Max(300)
    @Column(name = "tension_sistolica")
    private Integer tensionSistolica;

    @Min(20)
    @Max(200)
    @Column(name = "tension_diastolica")
    private Integer tensionDiastolica;

    @Min(20)
    @Max(250)
    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    @Min(5)
    @Max(60)
    @Column(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria;

    @Min(0)
    @Max(100)
    @Column(name = "saturacion_oxigeno")
    private Integer saturacionOxigeno;

    @DecimalMin("1.0")
    @Column(name = "peso_kg")
    private Double pesoKg;

    @DecimalMin("30.0")
    @Column(name = "talla_cm")
    private Double tallaCm;

    // ==========================================================
    // MODULO 4: EVALUACION DEL DOLOR (ESCALA EVA)
    // ==========================================================

    @Min(0)
    @Max(10)
    @Column(name = "intensidad_dolor")
    private Integer intensidadDolor;

    @ElementCollection(targetClass = TipoDolor.class, fetch = FetchType.EAGER)
    @CollectionTable(
            name = "historia_tipo_dolor",
            joinColumns = @JoinColumn(name = "id_historia_clinica")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dolor", length = 30)
    private Set<TipoDolor> tiposDolor = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "comportamiento_dolor", length = 30)
    private ComportamientoDolor comportamientoDolor;

    // ==========================================================
    // MODULO 5: DIAGNOSTICO Y OBJETIVOS TERAPEUTICOS
    // ==========================================================

    @Column(name = "diagnostico_motivo_consulta", columnDefinition = "TEXT")
    private String diagnosticoMotivoConsulta;

    @Column(name = "objetivos_terapeuticos", columnDefinition = "TEXT")
    private String objetivosTerapeuticos;

    // ==========================================================
    // CAMPOS CALCULADOS 
    // ==========================================================

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
