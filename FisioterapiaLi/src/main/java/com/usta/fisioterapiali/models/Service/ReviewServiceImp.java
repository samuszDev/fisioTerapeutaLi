package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ReviewEntity;
import com.usta.fisioterapiali.models.Daos.ReviewDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImp implements ReviewService {

    @Autowired
    private ReviewDao reviewDao;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewEntity> findAll() {
        return reviewDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewEntity findById(Long id) {
        return reviewDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ReviewEntity save(ReviewEntity review) {
        return reviewDao.save(review);
    }

    @Override
    @Transactional
    public ReviewEntity update(ReviewEntity review) {
        return reviewDao.save(review);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        reviewDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewEntity> findByUsuarioId(Long idUsuario) {
        return reviewDao.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewEntity> findByServicioId(Long idServicio) {
        return reviewDao.findByServicioIdServicio(idServicio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewEntity> findByCitaId(Long idReserva) {
        return reviewDao.findByCitaIdReserva(idReserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewEntity> findAllOrderByDateDesc() {
        return reviewDao.findAllByOrderByFechaHoraDesc();
    }
}