package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Reservas activas que se cruzan con el rango dado.
     * Dos intervalos se solapan si inicioA < finB Y finA > inicioB.
     * Se ignoran las canceladas (estadoR = false).
     */
    @Query("SELECT r FROM ReservationEntity r " +
            "WHERE r.estadoR = true " +
            "AND r.fechaInicio < :fin " +
            "AND r.fechaFin > :inicio")
    List<ReservationEntity> findSolapadas(@Param("inicio") LocalDateTime inicio,
                                          @Param("fin") LocalDateTime fin);
}