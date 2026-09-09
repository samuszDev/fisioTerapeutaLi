package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.RegistrationEntity;
import com.usta.fisioterapiali.entities.ReservationEntity;
import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.RegistrationDao;
import com.usta.fisioterapiali.models.Daos.ReservationDao;
import com.usta.fisioterapiali.models.Daos.ScheduleDao;
import com.usta.fisioterapiali.models.Daos.ServiceDao;
import com.usta.fisioterapiali.models.Daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.usta.fisioterapiali.entities.HistoriaClinicaEntity;
import com.usta.fisioterapiali.models.Daos.HistoriaClinicaDao;

import java.time.LocalDateTime;
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

    @Autowired
    private RegistrationDao registrationDao;

    @Autowired
    private HistoriaClinicaDao historiaClinicaDao;

    @Autowired
    private PasswordEncoder passwordEncoder; // <-- 1. Inyectamos el encriptador aquí


    private void registrarAccion(String origen, String accion) {
        try {
            RegistrationEntity registro = new RegistrationEntity();
            registro.setFechaHora(LocalDateTime.now());
            registro.setOrigen(origen);
            registro.setAccion(accion);
            List<UserEntity> usuarios = userDao.findAll();
            if (!usuarios.isEmpty()) {
                registro.setUsuario(usuarios.get(0));
            }
            registrationDao.save(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            registrarAccion("AGENDA", "Eliminación de cita ID: " + id);
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

            Long idServ = Long.parseLong(idServicioStr);
            Optional<ServiceEntity> servicioOpt = serviceDao.findById(idServ);
            if (servicioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El servicio seleccionado no existe.");
            }
            ServiceEntity servicio = servicioOpt.get();
            int duracionMinutos = servicio.getDuracionS() != null ? servicio.getDuracionS() : 60;

            java.time.LocalDateTime fechaHoraInicio = java.time.LocalDateTime.of(fecha, horaInicio);
            java.time.LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(duracionMinutos);

            int diaSemana = fecha.getDayOfWeek().getValue();
            Optional<com.usta.fisioterapiali.entities.ScheduleEntity> horarioOpt = scheduleDao.findByDiaSemana(diaSemana);

            if (horarioOpt.isPresent()) {
                com.usta.fisioterapiali.entities.ScheduleEntity horario = horarioOpt.get();

                if (horaInicio.isBefore(horario.getHoraApertura()) || fechaHoraFin.toLocalTime().isAfter(horario.getHoraCierre())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("La cita excede el horario de atención permitido (" + horario.getHoraApertura() + " - " + horario.getHoraCierre() + ").");
                }

                if (horario.getHoraInicioDescanso() != null && horario.getHoraFinDescanso() != null) {
                    boolean cruzaDescanso = fechaHoraInicio.toLocalTime().isBefore(horario.getHoraFinDescanso()) &&
                            fechaHoraFin.toLocalTime().isAfter(horario.getHoraInicioDescanso());
                    if (cruzaDescanso) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("El horario de la cita interfiere con el tiempo de almuerzo/descanso.");
                    }
                }
            }

            List<ReservationEntity> citasExistentes = reservationDao.findAll();
            for (ReservationEntity cita : citasExistentes) {
                if (cita.getFechaInicio() != null && cita.getFechaFin() != null) {
                    boolean seCruzan = fechaHoraInicio.isBefore(cita.getFechaFin()) && fechaHoraFin.isAfter(cita.getFechaInicio());
                    if (seCruzan) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Ya existe una cita programada en este intervalo.");
                    }
                }
            }

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
            registrarAccion("AGENDA", "Creación de cita para el servicio: " + servicio.getNombreS());
            return ResponseEntity.status(HttpStatus.CREATED).body("Cita guardada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al agendar la cita: " + e.getMessage());
        }
    }

    @PutMapping("/citas/{id}")
    public ResponseEntity<?> actualizarCita(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        try {
            Optional<ReservationEntity> citaOpt = reservationDao.findById(id);
            if (citaOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cita no encontrada.");
            }

            ReservationEntity citaActual = citaOpt.get();

            String fechaForm = payload.get("fecha");
            String horaForm = payload.get("hora");

            if (fechaForm != null && !fechaForm.isEmpty() && horaForm != null && !horaForm.isEmpty()) {
                java.time.LocalDate fecha = java.time.LocalDate.parse(fechaForm);
                java.time.LocalTime horaInicio = java.time.LocalTime.parse(horaForm);

                int duracionMinutos = citaActual.getServicio() != null && citaActual.getServicio().getDuracionS() != null
                        ? citaActual.getServicio().getDuracionS() : 60;

                java.time.LocalDateTime fechaHoraInicio = java.time.LocalDateTime.of(fecha, horaInicio);
                java.time.LocalDateTime fechaHoraFin = fechaHoraInicio.plusMinutes(duracionMinutos);

                int diaSemana = fecha.getDayOfWeek().getValue();
                Optional<com.usta.fisioterapiali.entities.ScheduleEntity> horarioOpt = scheduleDao.findByDiaSemana(diaSemana);

                if (horarioOpt.isPresent()) {
                    com.usta.fisioterapiali.entities.ScheduleEntity horario = horarioOpt.get();

                    if (horaInicio.isBefore(horario.getHoraApertura()) || fechaHoraFin.toLocalTime().isAfter(horario.getHoraCierre())) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("La nueva hora excede el horario de atención permitido.");
                    }

                    if (horario.getHoraInicioDescanso() != null && horario.getHoraFinDescanso() != null) {
                        boolean cruzaDescanso = fechaHoraInicio.toLocalTime().isBefore(horario.getHoraFinDescanso()) &&
                                fechaHoraFin.toLocalTime().isAfter(horario.getHoraInicioDescanso());
                        if (cruzaDescanso) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("El nuevo horario interfiere con el tiempo de descanso.");
                        }
                    }
                }

                List<ReservationEntity> citasExistentes = reservationDao.findAll();
                for (ReservationEntity c : citasExistentes) {
                    if (!c.getIdReserva().equals(id) && c.getFechaInicio() != null && c.getFechaFin() != null) {
                        boolean seCruzan = fechaHoraInicio.isBefore(c.getFechaFin()) && fechaHoraFin.isAfter(c.getFechaInicio());
                        if (seCruzan) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                    .body("Ya existe otra cita programada en este intervalo de tiempo.");
                        }
                    }
                }

                citaActual.setFechaInicio(fechaHoraInicio);
                citaActual.setFechaFin(fechaHoraFin);
            }

            if (payload.get("sede") != null) citaActual.setSede(payload.get("sede"));
            if (payload.get("estado") != null) citaActual.setEstadoCita(payload.get("estado"));
            if (payload.get("estadoPago") != null) citaActual.setPagoConfirmado("PAGADO".equalsIgnoreCase(payload.get("estadoPago")));

            reservationDao.save(citaActual);
            registrarAccion("AGENDA", "Actualización de cita ID: " + id);
            return ResponseEntity.ok("Cita actualizada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la cita: " + e.getMessage());
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

            String precioStr = payload.get("precio") != null ? payload.get("precio") : payload.get("precioIndividual");
            if (precioStr != null && !precioStr.isEmpty()) {
                servicio.setPrecioIndividual(Double.parseDouble(precioStr));
            }

            String duracionStr = payload.get("duracion") != null ? payload.get("duracion") : payload.get("duracionS");
            if (duracionStr != null && !duracionStr.isEmpty()) {
                String soloNumeros = duracionStr.replaceAll("[^0-9]", "");
                servicio.setDuracionS(soloNumeros.isEmpty() ? 30 : Integer.parseInt(soloNumeros));
            } else {
                servicio.setDuracionS(30);
            }

            String estadoStr = payload.get("estado") != null ? payload.get("estado") : String.valueOf(payload.get("estadoS"));
            servicio.setEstadoS(estadoStr == null || "true".equalsIgnoreCase(estadoStr) || "ACTIVO".equalsIgnoreCase(estadoStr));

            ServiceEntity nuevoServicio = serviceDao.save(servicio);
            registrarAccion("SERVICIOS", "Creación de servicio: " + servicio.getNombreS());
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el servicio: " + e.getMessage());
        }
    }

    @PutMapping("/servicios/{id}")
    public ResponseEntity<?> actualizarServicio(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            Optional<ServiceEntity> servicioOpt = serviceDao.findById(id);
            if (servicioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Servicio no encontrado.");
            }

            ServiceEntity servicio = servicioOpt.get();

            Object nombreObj = payload.get("nombreS") != null ? payload.get("nombreS") : payload.get("nombreService");
            if (nombreObj != null) {
                servicio.setNombreS(nombreObj.toString());
            }

            Object precioObj = payload.get("precioIndividual") != null ? payload.get("precioIndividual") : payload.get("precio");
            if (precioObj != null && !precioObj.toString().isEmpty()) {
                servicio.setPrecioIndividual(Double.parseDouble(precioObj.toString()));
            }

            Object duracionObj = payload.get("duracionS") != null ? payload.get("duracionS") : payload.get("duracion");
            if (duracionObj != null) {
                String duracionStr = duracionObj.toString();
                String soloNumeros = duracionStr.replaceAll("[^0-9]", "");
                servicio.setDuracionS(soloNumeros.isEmpty() ? null : Integer.parseInt(soloNumeros));
            }

            Object estadoObj = payload.get("estadoS") != null ? payload.get("estadoS") : payload.get("estado");
            if (estadoObj != null) {
                if (estadoObj instanceof Boolean) {
                    servicio.setEstadoS((Boolean) estadoObj);
                } else {
                    String estStr = estadoObj.toString();
                    servicio.setEstadoS(Boolean.parseBoolean(estStr) || "ACTIVO".equalsIgnoreCase(estStr));
                }
            }

            serviceDao.save(servicio);
            registrarAccion("SERVICIOS", "Actualización de servicio: " + servicio.getNombreS());
            return ResponseEntity.ok("Servicio actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el servicio: " + e.getMessage());
        }
    }

    @DeleteMapping("/servicios/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id) {
        try {
            if (!serviceDao.existsById(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Servicio no encontrado.");
            }
            serviceDao.deleteById(id);
            registrarAccion("SERVICIOS", "Eliminación de servicio ID: " + id);
            return ResponseEntity.ok("Servicio eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el servicio: " + e.getMessage());
        }
    }

    // --- MÉTODOS PARA PACIENTES ---

    @GetMapping("/pacientes")
    public List<UserEntity> obtenerPacientes() {
        return userDao.findByRol(TipoUsuario.CLIENTE);
    }

    @PostMapping("/pacientes")
    public ResponseEntity<?> guardarPaciente(@RequestBody Map<String, Object> payload) {
        try {
            String documento = (String) payload.get("documento");
            String nombre = (String) payload.get("nombre");
            String referidoPor = (String) payload.get("referidoPor"); // Campo opcional

            if (documento != null && !documento.isBlank()) {
                Optional<UserEntity> existePaciente = userDao.findByDocumento(documento);
                if (existePaciente.isPresent()) {
                    // Si el documento ya existe, incrementamos su contador de referidos
                    UserEntity clienteExistente = existePaciente.get();
                    int contadorActual = clienteExistente.getContadorReferidos() != null ? clienteExistente.getContadorReferidos() : 0;
                    clienteExistente.setContadorReferidos(contadorActual + 1);

                    userDao.save(clienteExistente);
                    registrarAccion("CLIENTES", "Incremento de referidos para el paciente: " + clienteExistente.getNombre());

                    return ResponseEntity.ok("El documento ya estaba registrado. Se ha incrementado el contador de referidos correctamente para: " + clienteExistente.getNombre());
                }
            }

            // Crear nuevo usuario si no existe
            UserEntity nuevoCliente = new UserEntity();
            nuevoCliente.setNombre(nombre);
            nuevoCliente.setDocumento(documento);


            if (payload.get("fechaNacimiento") != null && !((String) payload.get("fechaNacimiento")).isEmpty()) {
                nuevoCliente.setFechaNacimiento(java.time.LocalDate.parse((String) payload.get("fechaNacimiento")));
            }

            nuevoCliente.setGenero((String) payload.get("genero"));
            nuevoCliente.setEps((String) payload.get("eps"));
            nuevoCliente.setTelefono((String) payload.get("telefono"));
            nuevoCliente.setEmail((String) payload.get("email"));
            nuevoCliente.setContactoEmergencia((String) payload.get("contactoEmergencia"));

            // Opcional: Si ingresaron un documento de referido, puedes buscarlo o simplemente guardarlo si tienes el campo en la entidad
            if (referidoPor != null && !referidoPor.isBlank()) {
                Optional<UserEntity> referenteOpt = userDao.findByDocumento(referidoPor);
                if (referenteOpt.isPresent()) {
                    UserEntity referente = referenteOpt.get();
                    int refActual = referente.getContadorReferidos() != null ? referente.getContadorReferidos() : 0;
                    referente.setContadorReferidos(refActual + 1);
                    userDao.save(referente);
                }
            }

            nuevoCliente.setRol(TipoUsuario.CLIENTE);

            // 2. APLICAMOS LA ENCRIPTACIÓN AQUÍ. Usamos el documento como clave segura.
            nuevoCliente.setClave(passwordEncoder.encode(documento));

            nuevoCliente.setContadorReferidos(0);

            UserEntity clienteGuardado = userDao.save(nuevoCliente);
            registrarAccion("CLIENTES", "Creación de paciente: " + clienteGuardado.getNombre());
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);

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
            registrarAccion("CLIENTES", "Actualización de paciente: " + paciente.getNombre());
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
            registrarAccion("CLIENTES", "Eliminación de paciente ID: " + id);
            return ResponseEntity.ok("Paciente eliminado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el paciente: " + e.getMessage());
        }
    }

    //METODOS PARA HISTORIA CLINICA
    @GetMapping("/historia-clinica/{idUsuario}")
    public ResponseEntity<?> obtenerHistoriaClinica(@PathVariable Long idUsuario) {
        try {
            Optional<HistoriaClinicaEntity> historiaClinica = historiaClinicaDao.findByUsuario_IdUsuario(idUsuario);
            if (historiaClinica.isPresent()) {
                return ResponseEntity.ok(historiaClinica.get());
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Este paciente aún no tiene historia clínica registrada.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar la historia clínica: " + e.getMessage());
        }
    }

    @PostMapping("/historia-clinica/{idUsuario}")
    public ResponseEntity<?> guardarHistoriaClinica(@PathVariable Long idUsuario,
                                                    @RequestBody HistoriaClinicaEntity historiaClinica) {
        try {
            Optional<UserEntity> pacienteOpt = userDao.findById(idUsuario);
            if (pacienteOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El paciente indicado no existe.");
            }

            Optional<HistoriaClinicaEntity> existente = historiaClinicaDao.findByUsuario_IdUsuario(idUsuario);
            existente.ifPresent(hc -> historiaClinica.setIdHistoriaClinica(hc.getIdHistoriaClinica()));
            historiaClinica.setUsuario(pacienteOpt.get());

            HistoriaClinicaEntity guardada = historiaClinicaDao.save(historiaClinica);

            String accion = (existente.isPresent() ? "Actualización" : "Creación") + " de historia clínica";
            registrarAccion("HISTORIA_CLINICA", accion);

            HttpStatus status = existente.isPresent() ? HttpStatus.OK : HttpStatus.CREATED;
            return ResponseEntity.status(status).body(guardada);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la historia clínica: " + e.getMessage());
        }
    }


    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> obtenerEstadisticasDashboard() {
        try {
            java.time.LocalDate hoy = java.time.LocalDate.now();
            List<ReservationEntity> todasLasCitas = reservationDao.findAll();

            long citasHoy = todasLasCitas.stream()
                    .filter(c -> c.getFechaInicio() != null && c.getFechaInicio().toLocalDate().equals(hoy))
                    .count();

            long pacientesActivos = userDao.findByRol(TipoUsuario.CLIENTE).size();

            long pendientesPago = todasLasCitas.stream()
                    .filter(c -> !Boolean.TRUE.equals(c.getPagoConfirmado()))
                    .count();

            int referidosCompletados = userDao.findByRol(TipoUsuario.CLIENTE).stream()
                    .mapToInt(u -> u.getContadorReferidos() != null ? u.getContadorReferidos() : 0)
                    .sum();

            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("citasHoy", citasHoy);
            stats.put("pacientesActivos", pacientesActivos);
            stats.put("pendientesPago", pendientesPago);
            stats.put("referidosCompletados", referidosCompletados);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener estadísticas: " + e.getMessage());
        }
    }

    @GetMapping("/reportes/historial")
    public ResponseEntity<?> obtenerHistorial() {
        try {
            List<RegistrationEntity> listaRegistros = registrationDao.findAllByOrderByFechaHoraDesc();
            return ResponseEntity.ok(listaRegistros);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar el historial: " + e.getMessage());
        }
    }
}