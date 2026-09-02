package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.ScheduleEntity;
import com.usta.fisioterapiali.models.Service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleEntity>> getAllSchedules() {
        List<ScheduleEntity> schedules = scheduleService.findAllOrderByDiaSemanaAsc();
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleEntity> getScheduleById(@PathVariable Long id) {
        ScheduleEntity schedule = scheduleService.findById(id);
        if (schedule != null) {
            return new ResponseEntity<>(schedule, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/dia/{diaSemana}")
    public ResponseEntity<ScheduleEntity> getScheduleByDay(@PathVariable Integer diaSemana) {
        Optional<ScheduleEntity> schedule = scheduleService.findByDiaSemana(diaSemana);
        if (schedule.isPresent()) {
            return new ResponseEntity<>(schedule.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/sede/{sede}")
    public ResponseEntity<List<ScheduleEntity>> getSchedulesBySede(@PathVariable String sede) {
        List<ScheduleEntity> schedules = scheduleService.findBySede(sede);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ScheduleEntity> createSchedule(@RequestBody ScheduleEntity schedule) {
        ScheduleEntity newSchedule = scheduleService.save(schedule);
        return new ResponseEntity<>(newSchedule, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleEntity> updateSchedule(@PathVariable Long id, @RequestBody ScheduleEntity scheduleDetails) {
        ScheduleEntity existingSchedule = scheduleService.findById(id);
        if (existingSchedule == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        existingSchedule.setDiaSemana(scheduleDetails.getDiaSemana());
        existingSchedule.setSede(scheduleDetails.getSede());
        existingSchedule.setHoraApertura(scheduleDetails.getHoraApertura());
        existingSchedule.setHoraCierre(scheduleDetails.getHoraCierre());
        existingSchedule.setHoraInicioDescanso(scheduleDetails.getHoraInicioDescanso());
        existingSchedule.setHoraFinDescanso(scheduleDetails.getHoraFinDescanso());
        existingSchedule.setTiempoAnticipado(scheduleDetails.getTiempoAnticipado());

        ScheduleEntity updatedSchedule = scheduleService.update(existingSchedule);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        ScheduleEntity schedule = scheduleService.findById(id);
        if (schedule == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        scheduleService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}