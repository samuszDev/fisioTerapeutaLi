package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationDao extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByUsuario_IdUsuario(Long idUsuario);
    List<ReservationEntity> findByServicio_IdServicio(Long idServicio);
    List<ReservationEntity> findBySede(String sede);
    List<ReservationEntity> findByPagoConfirmado(Boolean pagoConfirmado);
    List<ReservationEntity> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    Optional<ReservationEntity> findByGoogleCalendarId(String googleCalendarId);
    List<ReservationEntity> findAllByOrderByFechaInicioAsc();
}