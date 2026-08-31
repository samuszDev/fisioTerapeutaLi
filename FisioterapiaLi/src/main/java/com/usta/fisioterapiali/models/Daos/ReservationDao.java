package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationDao extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByUsuarioIdUsuario(Long idUsuario);
    List<ReservationEntity> findByServicioIdServicio(Long idServicio);
    List<ReservationEntity> findByEstadoRTrue();
    Optional<ReservationEntity> findByGoogleCalendarId(String googleCalendarId);
    List<ReservationEntity> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    List<ReservationEntity> findAllByOrderByFechaInicioAsc();
}