package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ReviewEntity;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewEntity> findAll();
    ReviewEntity findById(Long id);
    ReviewEntity save(ReviewEntity review);
    ReviewEntity update(ReviewEntity review);
    void deleteById(Long id);
    List<ReviewEntity> findByUsuarioId(Long idUsuario);
    List<ReviewEntity> findByServicioId(Long idServicio);
    Optional<ReviewEntity> findByCitaId(Long idReserva);
    List<ReviewEntity> findAllOrderByDateDesc();
}