package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ReservationEntity;
import com.usta.fisioterapiali.models.Daos.ReservationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImp implements ReservationService {

    @Autowired
    private ReservationDao reservationDao;

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> findAll() {
        return reservationDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationEntity findById(Long id) {
        return reservationDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ReservationEntity save(ReservationEntity reservation) {
        return reservationDao.save(reservation);
    }

    @Override
    @Transactional
    public ReservationEntity update(ReservationEntity reservation) {
        return reservationDao.save(reservation);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        reservationDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> findByUsuarioId(Long idUsuario) {
        return reservationDao.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> findByServicioId(Long idServicio) {
        return reservationDao.findByServicio_IdServicio(idServicio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> findBySede(String sede) {
        return reservationDao.findBySede(sede);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> findByPagoConfirmado(Boolean pagoConfirmado) {
        return reservationDao.findByPagoConfirmado(pagoConfirmado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin) {
        return reservationDao.findByFechaInicioBetween(inicio, fin);
    }
    @Override
    @Transactional(readOnly = true)
    public Optional<ReservationEntity> findByGoogleCalendarId(String googleCalendarId) {
        return reservationDao.findByGoogleCalendarId(googleCalendarId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationEntity> findAllByOrderByFechaInicioAsc() {
        return reservationDao.findAllByOrderByFechaInicioAsc();
    }
}