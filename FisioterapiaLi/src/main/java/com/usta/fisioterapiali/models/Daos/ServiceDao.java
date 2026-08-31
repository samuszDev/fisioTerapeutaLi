package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceDao extends JpaRepository<ServiceEntity, Long> {
    List<ServiceEntity> findByEstadoSTrue();
    Optional<ServiceEntity> findByNombreS(String nombreS);
    List<ServiceEntity> findAllByOrderByNombreSAsc();
    boolean existsByNombreS(String nombreS);
}