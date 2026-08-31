package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.RegistrationEntity;
import com.usta.fisioterapiali.models.Daos.RegistrationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationServiceImp implements RegistrationService {

    @Autowired
    private RegistrationDao registrationDao;

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationEntity> findAll() {
        return registrationDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationEntity findById(Long id) {
        return registrationDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public RegistrationEntity save(RegistrationEntity registration) {
        return registrationDao.save(registration);
    }

    @Override
    @Transactional
    public RegistrationEntity update(RegistrationEntity registration) {
        return registrationDao.save(registration);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        registrationDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationEntity> findAllOrderByDateDesc() {
        return registrationDao.findAllByOrderByFechaHoraDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationEntity> findByUserId(Long idUsuario) {
        return registrationDao.findByUsuarioIdUsuario(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegistrationEntity> findByOrigen(String origen) {
        return registrationDao.findByOrigen(origen);
    }
}