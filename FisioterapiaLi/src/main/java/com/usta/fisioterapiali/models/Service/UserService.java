package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserEntity> findAll();
    UserEntity findById(Long id);
    UserEntity save(UserEntity user);
    UserEntity update(UserEntity user);
    void deleteById(Long id);
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByRol(TipoUsuario rol);
    boolean existsByEmail(String email);
}