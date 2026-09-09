package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.HistoriaClinicaEntity;
import com.usta.fisioterapiali.enums.MotivoConsulta;
import com.usta.fisioterapiali.models.Daos.HistoriaClinicaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HistoriaClinicaServiceImp implements HistoriaClinicaService {

    @Autowired
    private HistoriaClinicaDao historiaClinicaDao;

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaEntity> findAll() {
        return historiaClinicaDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public HistoriaClinicaEntity findById(Long id) {
        return historiaClinicaDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public HistoriaClinicaEntity save(HistoriaClinicaEntity historiaClinica) {
        return historiaClinicaDao.save(historiaClinica);
    }

    @Override
    @Transactional
    public HistoriaClinicaEntity update(HistoriaClinicaEntity historiaClinica) {
        return historiaClinicaDao.save(historiaClinica);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        historiaClinicaDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistoriaClinicaEntity> findByUsuarioId(Long idUsuario) {
        return historiaClinicaDao.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsuarioId(Long idUsuario) {
        return historiaClinicaDao.existsByUsuario_IdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoriaClinicaEntity> findByMotivoPrincipalConsulta(MotivoConsulta motivoPrincipalConsulta) {
        return historiaClinicaDao.findByMotivoPrincipalConsulta(motivoPrincipalConsulta);
    }
}
