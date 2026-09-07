package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.ReservationEntity;
import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.ReservationDao;
import com.usta.fisioterapiali.models.Daos.ScheduleDao;
import com.usta.fisioterapiali.models.Daos.ServiceDao;
import com.usta.fisioterapiali.models.Daos.UserDao;
import com.usta.fisioterapiali.models.Service.UserService;
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

    @Autowired
    private ScheduleDao scheduleDao;

    // --- MÉTODOS PARA CITAS / AGENDA ---

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
    @DeleteMapping("/citas/{id}")
    public ResponseEntity<?> eliminarCita(@PathVariable Long id) {
        try {
            if (!reservationDao.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
            }
            reservationDao.deleteById(id);
            return ResponseEntity.ok("Cita eliminada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la cita: " + e.getMessage());
        }
    }

    @PostMapping("/citas")
    public ResponseEntity<?> guardarCita(@RequestBody Map<String, String> payload) {
        try {
            String fechaForm = payload.get("fecha");
            String horaForm = payload.get("hora");
            String idServicioStr = payload.get("idServicio");

            if (fechaForm == null || fechaForm.isEmpty() || horaForm == null || horaForm.isEmpty() || idServicioStr == null || idServicioStr.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Todos los campos obligatorios (fecha, hora y servicio) son requeridos.");
            }

            java.time.LocalDate fecha = java.time.LocalDate.parse(fechaForm);
            java.time.LocalTime horaInicio = java.time.LocalTime.parse(horaForm);

            // 1. OBTENER LA DURACIÓN REAL DEL SERVICIO SELECCIONADO (Por defecto 60 min si no se encuentra)
            Long idServ = Long.parseLong(idServicioStr);
            Optional<ServiceEntity> servicioOpt = serviceDao.findById(idServ);
            if (servicioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El servicio seleccionado no existe.");
            }
            ServiceEntity servicio = servicioOpt.get();
            int duracionMinutos = servicio.getDuracionS() != null ? servicio.getDuracionS() : 60;

            java.time.LocalDateTime fechaHoraInicio = java.time.LocalDateTime.of(fecha, horaInicio);
            java.time.LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(duracionMinutos);

            // 2. VERIFICAR HORARIO LABORAL Y DESCANSO USANDO EL ScheduleDao
            int diaSemana = fecha.getDayOfWeek().getValue(); // 1 = Lunes ... 7 = Domingo
            Optional<com.usta.fisioterapiali.entities.ScheduleEntity> horarioOpt = scheduleDao.findByDiaSemana(diaSemana);

            if (horarioOpt.isPresent()) {
                com.usta.fisioterapiali.entities.ScheduleEntity horario = horarioOpt.get();

                // Validar apertura y cierre de la jornada
                if (horaInicio.isBefore(horario.getHoraApertura()) || fechaHoraFin.toLocalTime().isAfter(horario.getHoraCierre())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("La cita excede el horario de atención permitido (" + horario.getHoraApertura() + " - " + horario.getHoraCierre() + ").");
                }

                // Validar hora de descanso (almuerzo) si aplica
                if (horario.getHoraInicioDescanso() != null && horario.getHoraFinDescanso() != null) {
                    boolean cruzaDescanso = fechaHoraInicio.toLocalTime().isBefore(horario.getHoraFinDescanso()) &&
                            fechaHoraFin.toLocalTime().isAfter(horario.getHoraInicioDescanso());
                    if (cruzaDescanso) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("El horario de la cita interfiere con el tiempo de almuerzo/descanso (" + horario.getHoraInicioDescanso() + " - " + horario.getHoraFinDescanso() + ").");
                    }
                }
            }

            // 3. VALIDAR SOLAPAMIENTO DE TIEMPO CON CITAS EXISTENTES (CRUCE DE RANGOS)
            List<ReservationEntity> citasExistentes = reservationDao.findAll();
            for (ReservationEntity cita : citasExistentes) {
                if (cita.getFechaInicio() != null && cita.getFechaFin() != null) {
                    // Dos rangos de tiempo se cruzan si: (InicioA < FinB) y (FinA > InicioB)
                    boolean seCruzan = fechaHoraInicio.isBefore(cita.getFechaFin()) && fechaHoraFin.isAfter(cita.getFechaInicio());

                    if (seCruzan) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Ya existe una cita programada en este intervalo (" +
                                        cita.getFechaInicio().toLocalTime() + " - " + cita.getFechaFin().toLocalTime() +
                                        "). Debe esperar a que finalice el servicio actual.");
                    }
                }
            }

            // 4. GUARDAR LA CITA SI EL ESPACIO ESTÁ COMPLETAMENTE LIBRE
            ReservationEntity nuevaCita = new ReservationEntity();

            if (payload.get("idUsuario") != null && !payload.get("idUsuario").isEmpty()) {
                Long idUser = Long.parseLong(payload.get("idUsuario"));
                userDao.findById(idUser).ifPresent(nuevaCita::setUsuario);
            }

            nuevaCita.setServicio(servicio);
            nuevaCita.setSede(payload.get("sede"));
            nuevaCita.setEstadoCita("CONFIRMADA");
            nuevaCita.setGoogleCalendarId("N/A");

            String estadoPagoStr = payload.get("estadoPago");
            nuevaCita.setPagoConfirmado("PAGADO".equalsIgnoreCase(estadoPagoStr));

            nuevaCita.setFechaInicio(fechaHoraInicio);
            nuevaCita.setFechaFin(fechaHoraFin);

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

    @PostMapping("/servicios")
    public ResponseEntity<?> guardarServicio(@RequestBody Map<String, String> payload) {
        try {
            ServiceEntity servicio = new ServiceEntity();

            String nombre = payload.get("nombreService") != null ? payload.get("nombreService") : payload.get("nombreS");
            servicio.setNombreS(nombre);

            if (payload.get("precio") != null && !payload.get("precio").isEmpty()) {
                servicio.setPrecioIndividual(Double.parseDouble(payload.get("precio")));
            }

            String duracionStr = payload.get("duracion");
            if (duracionStr != null && !duracionStr.isEmpty()) {
                String soloNumeros = duracionStr.replaceAll("[^0-9]", "");
                servicio.setDuracionS(soloNumeros.isEmpty() ? 30 : Integer.parseInt(soloNumeros));
            } else {
                servicio.setDuracionS(30);
            }

            String estadoStr = payload.get("estado");
            servicio.setEstadoS(estadoStr == null || "ACTIVO".equalsIgnoreCase(estadoStr));

            ServiceEntity nuevoServicio = serviceDao.save(servicio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el servicio: " + e.getMessage());
        }
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

    @PutMapping("/pacientes/{id}")
    public ResponseEntity<?> actualizarPaciente(@PathVariable Long id, @RequestBody UserEntity clienteActualizado) {
        try {
            Optional<UserEntity> pacienteOpt = userDao.findById(id);
            if (pacienteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado.");
            }

            UserEntity paciente = pacienteOpt.get();
            paciente.setNombre(clienteActualizado.getNombre());
            paciente.setDocumento(clienteActualizado.getDocumento());
            paciente.setTelefono(clienteActualizado.getTelefono());
            paciente.setEmail(clienteActualizado.getEmail());
            paciente.setEps(clienteActualizado.getEps());
            paciente.setGenero(clienteActualizado.getGenero());
            paciente.setContactoEmergencia(clienteActualizado.getContactoEmergencia());

            userDao.save(paciente);
            return ResponseEntity.ok("Paciente actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el paciente: " + e.getMessage());
        }
    }

    @DeleteMapping("/pacientes/{id}")
    public ResponseEntity<?> eliminarPaciente(@PathVariable Long id) {
        try {
            if (!userDao.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Paciente no encontrado.");
            }
            userDao.deleteById(id);
            return ResponseEntity.ok("Paciente eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el paciente: " + e.getMessage());
        }
    }
}