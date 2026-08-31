package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.ServiceEntity;

import java.util.List;
import java.util.Optional;

public interface ServiceService {
    List<ServiceEntity> findAll();
    List<ServiceEntity> findActiveServices();
    ServiceEntity findById(Long id);
    ServiceEntity save(ServiceEntity service);
    ServiceEntity update(ServiceEntity service);
    void deleteById(Long id);
    Optional<ServiceEntity> findByNombreS(String nombreS);
    boolean existsByNombreS(String nombreS);
}