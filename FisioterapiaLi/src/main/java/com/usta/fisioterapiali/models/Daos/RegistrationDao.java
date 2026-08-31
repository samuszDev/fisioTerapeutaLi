package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.RegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationDao extends JpaRepository<RegistrationEntity, Long> {

    List<RegistrationEntity> findAllByOrderByFechaHoraDesc();
    List<RegistrationEntity> findByUsuarioIdUsuario(Long idUsuario);
    List<RegistrationEntity> findByOrigen(String origen);
}