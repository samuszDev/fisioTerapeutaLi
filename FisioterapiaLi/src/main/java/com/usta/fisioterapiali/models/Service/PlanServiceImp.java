package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.PlanEntity;
import com.usta.fisioterapiali.models.Daos.PlanDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanServiceImp implements PlanService {

    @Autowired
    private PlanDao planDao;

    @Override
    @Transactional(readOnly = true)
    public List<PlanEntity> findAll() {
        return planDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanEntity findById(Long id) {
        return planDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public PlanEntity save(PlanEntity plan) {
        return planDao.save(plan);
    }

    @Override
    @Transactional
    public PlanEntity update(PlanEntity plan) {
        return planDao.save(plan);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        planDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanEntity> findByServicioId(Long idServicio) {
        return planDao.findByServicio_IdServicio(idServicio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanEntity> findGrupales() {
        return planDao.findByEsGrupalTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanEntity> findIndividuales() {
        return planDao.findByEsGrupalFalse();
    }
}