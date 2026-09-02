package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ReservationEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    List<ReservationEntity> findAll();
    ReservationEntity findById(Long id);
    ReservationEntity save(ReservationEntity reservation);
    ReservationEntity update(ReservationEntity reservation);
    void deleteById(Long id);

    List<ReservationEntity> findByUsuarioId(Long idUsuario);
    List<ReservationEntity> findByServicioId(Long idServicio);
    List<ReservationEntity> findBySede(String sede);
    List<ReservationEntity> findByPagoConfirmado(Boolean pagoConfirmado);
    List<ReservationEntity> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);
    Optional<ReservationEntity> findByGoogleCalendarId(String googleCalendarId);
    List<ReservationEntity> findAllByOrderByFechaInicioAsc();
}