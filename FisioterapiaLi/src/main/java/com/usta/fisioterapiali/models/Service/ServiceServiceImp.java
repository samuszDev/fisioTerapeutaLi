package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.models.Daos.ServiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceServiceImp implements ServiceService {

    @Autowired
    private ServiceDao serviceDao;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceEntity> findAll() {
        return serviceDao.findAllByOrderByNombreSAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceEntity> findActiveServices() {
        return serviceDao.findByEstadoSTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceEntity findById(Long id) {
        return serviceDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ServiceEntity save(ServiceEntity service) {
        return serviceDao.save(service);
    }

    @Override
    @Transactional
    public ServiceEntity update(ServiceEntity service) {
        return serviceDao.save(service);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        serviceDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceEntity> findByNombreS(String nombreS) {
        return serviceDao.findByNombreS(nombreS);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombreS(String nombreS) {
        return serviceDao.existsByNombreS(nombreS);
    }
}