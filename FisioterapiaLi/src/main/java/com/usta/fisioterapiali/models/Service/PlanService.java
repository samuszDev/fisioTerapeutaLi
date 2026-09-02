package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.PlanEntity;

import java.util.List;

public interface PlanService {
    List<PlanEntity> findAll();
    PlanEntity findById(Long id);
    PlanEntity save(PlanEntity plan);
    PlanEntity update(PlanEntity plan);
    void deleteById(Long id);
    List<PlanEntity> findByServicioId(Long idServicio);
    List<PlanEntity> findGrupales();
    List<PlanEntity> findIndividuales();
}