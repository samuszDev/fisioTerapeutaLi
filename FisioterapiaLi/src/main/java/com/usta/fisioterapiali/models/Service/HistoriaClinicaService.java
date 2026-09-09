package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.HistoriaClinicaEntity;
import com.usta.fisioterapiali.enums.MotivoConsulta;

import java.util.List;
import java.util.Optional;

public interface HistoriaClinicaService {
    List<HistoriaClinicaEntity> findAll();
    HistoriaClinicaEntity findById(Long id);
    HistoriaClinicaEntity save(HistoriaClinicaEntity historiaClinica);
    HistoriaClinicaEntity update(HistoriaClinicaEntity historiaClinica);
    void deleteById(Long id);
    Optional<HistoriaClinicaEntity> findByUsuarioId(Long idUsuario);
    boolean existsByUsuarioId(Long idUsuario);
    List<HistoriaClinicaEntity> findByMotivoPrincipalConsulta(MotivoConsulta motivoPrincipalConsulta);
}
