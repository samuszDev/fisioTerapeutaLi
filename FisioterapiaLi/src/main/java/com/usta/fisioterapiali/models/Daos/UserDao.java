package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<UserEntity, Long> {
    UserEntity findByEmail(String email);
    List<UserEntity> findAllByTelegramUserId(String telegramUserId);
    boolean existsByEmail(String email);
    List<UserEntity> findByRol(TipoUsuario rol);
    List<UserEntity> findAllByOrderByNombreAsc();
    Optional<UserEntity> findByDocumento(String documento);
    boolean existsByDocumento(String documento);
    
}