package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewDao extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByUsuarioIdUsuario(Long idUsuario);
    List<ReviewEntity> findByServicioIdServicio(Long idServicio);
    Optional<ReviewEntity> findByCitaIdReserva(Long idReserva);
    List<ReviewEntity> findAllByOrderByFechaHoraDesc();
}