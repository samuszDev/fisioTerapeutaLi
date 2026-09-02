package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleDao extends JpaRepository<ScheduleEntity, Long> {

    Optional<ScheduleEntity> findByDiaSemana(Integer diaSemana);
    List<ScheduleEntity> findBySede(String sede);
    List<ScheduleEntity> findAllByOrderByDiaSemanaAsc();
}