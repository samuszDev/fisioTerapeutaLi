package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ScheduleEntity;
import com.usta.fisioterapiali.models.Daos.ScheduleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleServiceImp implements ScheduleService {

    @Autowired
    private ScheduleDao scheduleDao;

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleEntity> findAll() {
        return scheduleDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ScheduleEntity findById(Long id) {
        return scheduleDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ScheduleEntity save(ScheduleEntity schedule) {
        return scheduleDao.save(schedule);
    }

    @Override
    @Transactional
    public ScheduleEntity update(ScheduleEntity schedule) {
        return scheduleDao.save(schedule);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        scheduleDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduleEntity> findByDiaSemana(Integer diaSemana) {
        return scheduleDao.findByDiaSemana(diaSemana);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleEntity> findAllOrderByDiaSemanaAsc() {
        return scheduleDao.findAllByOrderByDiaSemanaAsc();
    }
}