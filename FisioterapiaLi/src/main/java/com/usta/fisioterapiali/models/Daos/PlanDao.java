package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.PlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanDao extends JpaRepository<PlanEntity, Long> {

    List<PlanEntity> findByServicio_IdServicio(Long idServicio);
    List<PlanEntity> findByEsGrupalTrue();
    List<PlanEntity> findByEsGrupalFalse();
}