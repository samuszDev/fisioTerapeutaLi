package com.usta.fisioterapiali.components;

import com.usta.fisioterapiali.entities.ScheduleEntity;
import com.usta.fisioterapiali.models.Daos.ScheduleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
@Order(3)
public class HorarioDataInitializer implements CommandLineRunner {

    @Autowired
    private ScheduleDao scheduleDao;

    @Override
    public void run(String... args) throws Exception {
        if (scheduleDao.count() > 0) return;

        // 1 = lunes ... 7 = domingo (java.time.DayOfWeek.getValue())
        for (int dia = 1; dia <= 7; dia++) {
            ScheduleEntity h = new ScheduleEntity();
            h.setDiaSemana(dia);
            h.setHoraApertura(LocalTime.of(7, 0));
            h.setHoraCierre(LocalTime.of(20, 0));
            h.setHoraInicioDescanso(LocalTime.of(12, 0));
            h.setHoraFinDescanso(LocalTime.of(14, 0));
            h.setTiempoAnticipado(120);
            h.setSede(dia >= 6 ? "Turmequé" : "Tunja");
            scheduleDao.save(h);
        }
        System.out.println(">>> Horarios cargados: 7 días, 7:00-20:00, descanso 12:00-14:00 <<<");
    }
}