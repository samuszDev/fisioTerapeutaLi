package com.usta.fisioterapiali.util;

import com.usta.fisioterapiali.enums.*;

/**
 * Esta es una utilidad, encargada de calcular los "semaforos"/estados clinicos
 * a partir de los valores numericos crudos que digita el usuario.
 */
public class SignosVitalesCalculator {

    private SignosVitalesCalculator() {
    }

    public static EstadoTensionArterial calcularEstadoTensionArterial(Integer sistolica, Integer diastolica) {
        if (sistolica == null || diastolica == null) return null;

        if (sistolica < 90 || diastolica < 60) {
            return EstadoTensionArterial.HIPOTENSION;
        } else if (sistolica >= 140 || diastolica >= 90) {
            return EstadoTensionArterial.HTA_ETAPA_2;
        } else if (sistolica >= 130 || diastolica >= 80) {
            return EstadoTensionArterial.HTA_ETAPA_1;
        } else if (sistolica >= 120 && diastolica < 80) {
            return EstadoTensionArterial.ELEVADA;
        } else {
            return EstadoTensionArterial.NORMOTENSION;
        }
    }

    public static EstadoFrecuenciaCardiaca calcularEstadoFrecuenciaCardiaca(Integer fc) {
        if (fc == null) return null;
        if (fc < 60) return EstadoFrecuenciaCardiaca.BRADICARDIA;
        if (fc > 100) return EstadoFrecuenciaCardiaca.TAQUICARDIA;
        return EstadoFrecuenciaCardiaca.NORMOCARDIA;
    }

    public static EstadoFrecuenciaRespiratoria calcularEstadoFrecuenciaRespiratoria(Integer fr) {
        if (fr == null) return null;
        if (fr < 12) return EstadoFrecuenciaRespiratoria.BRADIPNEA;
        if (fr > 20) return EstadoFrecuenciaRespiratoria.TAQUIPNEA;
        return EstadoFrecuenciaRespiratoria.EUPNEA;
    }

    public static EstadoSaturacionOxigeno calcularEstadoSaturacionOxigeno(Integer spo2) {
        if (spo2 == null) return null;
        if (spo2 < 90) return EstadoSaturacionOxigeno.HIPOXIA_SEVERA;
        if (spo2 <= 94) return EstadoSaturacionOxigeno.HIPOXIA_LEVE;
        return EstadoSaturacionOxigeno.NORMAL;
    }

    /**
     * @param pesoKg  peso en kilogramos
     * @param tallaCm talla en centimetros
     */
    public static Double calcularIMC(Double pesoKg, Double tallaCm) {
        if (pesoKg == null || tallaCm == null || tallaCm == 0) return null;
        double tallaM = tallaCm / 100.0;
        double imc = pesoKg / (tallaM * tallaM);
        return Math.round(imc * 100.0) / 100.0; // 2 decimales
    }

    public static EstadoIMC calcularEstadoIMC(Double imc) {
        if (imc == null) return null;
        if (imc < 18.5) return EstadoIMC.BAJO_PESO;
        if (imc < 25.0) return EstadoIMC.NORMAL;
        if (imc < 30.0) return EstadoIMC.SOBREPESO;
        return EstadoIMC.OBESIDAD;
    }

    public static NivelDolorEva calcularNivelDolorEva(Integer intensidad) {
        if (intensidad == null) return null;
        if (intensidad == 0) return NivelDolorEva.SIN_DOLOR;
        if (intensidad <= 3) return NivelDolorEva.LEVE;
        if (intensidad <= 6) return NivelDolorEva.MODERADO;
        if (intensidad <= 9) return NivelDolorEva.SEVERO;
        return NivelDolorEva.INAGUANTABLE;
    }
}
