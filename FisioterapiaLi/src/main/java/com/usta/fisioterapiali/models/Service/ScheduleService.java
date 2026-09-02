package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ScheduleEntity;

import java.util.List;
import java.util.Optional;

public interface ScheduleService {

    List<ScheduleEntity> findAll();

    ScheduleEntity findById(Long id);

    ScheduleEntity save(ScheduleEntity schedule);

    ScheduleEntity update(ScheduleEntity schedule);

    void deleteById(Long id);

    Optional<ScheduleEntity> findByDiaSemana(Integer diaSemana);
    List<ScheduleEntity> findBySede(String sede);
    List<ScheduleEntity> findAllOrderByDiaSemanaAsc();
}