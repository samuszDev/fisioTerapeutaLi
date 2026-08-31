package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.RegistrationEntity;

import java.util.List;

public interface RegistrationService {
    List<RegistrationEntity> findAll();
    RegistrationEntity findById(Long id);
    RegistrationEntity save(RegistrationEntity registration);
    RegistrationEntity update(RegistrationEntity registration);
    void deleteById(Long id);
    List<RegistrationEntity> findAllOrderByDateDesc();
    List<RegistrationEntity> findByUserId(Long idUsuario);
    List<RegistrationEntity> findByOrigen(String origen);
}