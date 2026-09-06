package com.usta.fisioterapiali.models.Daos;

import com.usta.fisioterapiali.entities.HistoriaClinicaEntity;
import com.usta.fisioterapiali.enums.EpsAseguradora;
import com.usta.fisioterapiali.enums.MotivoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HistoriaClinicaDao extends JpaRepository<HistoriaClinicaEntity, Long> {
    Optional<HistoriaClinicaEntity> findByUsuario_IdUsuario(Long idUsuario);
    boolean existsByUsuario_IdUsuario(Long idUsuario);
    List<HistoriaClinicaEntity> findByMotivoPrincipalConsulta(MotivoConsulta motivoPrincipalConsulta);
    List<HistoriaClinicaEntity> findByEpsAseguradora(EpsAseguradora epsAseguradora);
    List<HistoriaClinicaEntity> findAllByOrderByIdHistoriaClinicaDesc();
}
