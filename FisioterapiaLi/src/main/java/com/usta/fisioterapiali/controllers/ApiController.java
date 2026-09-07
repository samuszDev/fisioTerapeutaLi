package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.ReservationEntity;
import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.ReservationDao;
import com.usta.fisioterapiali.models.Daos.ServiceDao;
import com.usta.fisioterapiali.models.Daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/modal")
public class ApiController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ServiceDao serviceDao;

    @Autowired
    private ReservationDao reservationDao;

    // --- MÉTODOS PARA CITAS / AGENDA ---

    // Este método evita el error 405 de lectura
    @GetMapping("/citas")
    public ResponseEntity<?> obtenerCitas() {
        try {
            List<ReservationEntity> listaCitas = reservationDao.findAll();
            return ResponseEntity.ok(listaCitas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar citas: " + e.getMessage());
        }
    }

    // Este método guarda la cita
    @PostMapping("/citas")
    public ResponseEntity<?> guardarCita(@RequestBody Map<String, String> payload) {
        try {
            ReservationEntity nuevaCita = new ReservationEntity();

            if (payload.get("idUsuario") != null && !payload.get("idUsuario").isEmpty()) {
                Long idUser = Long.parseLong(payload.get("idUsuario"));
                userDao.findById(idUser).ifPresent(nuevaCita::setUsuario);
            }

            if (payload.get("idServicio") != null && !payload.get("idServicio").isEmpty()) {
                Long idServ = Long.parseLong(payload.get("idServicio"));
                serviceDao.findById(idServ).ifPresent(nuevaCita::setServicio);
            }

            nuevaCita.setSede(payload.get("sede"));
            nuevaCita.setEstadoCita("CONFIRMADA");

            nuevaCita.setGoogleCalendarId("N/A");

            String estadoPagoStr = payload.get("estadoPago");
            nuevaCita.setPagoConfirmado("PAGADO".equalsIgnoreCase(estadoPagoStr));

            String fechaForm = payload.get("fecha");
            String horaForm = payload.get("hora");

            if (fechaForm != null && horaForm != null) {
                java.time.LocalDateTime fechaHoraInicio = java.time.LocalDateTime.parse(fechaForm + "T" + horaForm);
                nuevaCita.setFechaInicio(fechaHoraInicio);
                nuevaCita.setFechaFin(fechaHoraInicio.plusHours(1));
            }

            reservationDao.save(nuevaCita);
            return ResponseEntity.status(HttpStatus.CREATED).body("Cita guardada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agendar la cita: " + e.getMessage());
        }
    }

    // --- MÉTODOS PARA SERVICIOS ---

    @GetMapping("/servicios")
    public List<ServiceEntity> obtenerServicios() {
        return serviceDao.findAll();
    }

    // --- MÉTODOS PARA PACIENTES ---

    @GetMapping("/pacientes")
    public List<UserEntity> obtenerPacientes() {
        return userDao.findByRol(TipoUsuario.CLIENTE);
    }

    @PostMapping("/pacientes")
    public ResponseEntity<?> guardarPaciente(@RequestBody UserEntity cliente) {
        try {
            if (cliente.getDocumento() != null && !cliente.getDocumento().isBlank()) {
                Optional<UserEntity> existePaciente = userDao.findByDocumento(cliente.getDocumento());
                if (existePaciente.isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El número de documento ya está registrado.");
                }
            }

            if (cliente.getRol() == null) cliente.setRol(TipoUsuario.CLIENTE);
            if (cliente.getClave() == null || cliente.getClave().isBlank()) cliente.setClave("123456");
            if (cliente.getContadorReferidos() == null) cliente.setContadorReferidos(0);

            UserEntity nuevoCliente = userDao.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }
}