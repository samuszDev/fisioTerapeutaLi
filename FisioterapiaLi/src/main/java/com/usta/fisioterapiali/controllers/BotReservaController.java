package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.dto.PreReservaRequest;
import com.usta.fisioterapiali.dto.PreReservaResponse;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Punto de entrada del bot de Telegram y del formulario web.
 *
 * A diferencia de ReservationController (CRUD crudo), este endpoint aplica
 * las reglas de negocio: valida disponibilidad, deduce sede, calcula la hora
 * de fin según el servicio y crea el paciente si no existe.
 *
 * Principio: la IA interpreta, el backend valida, n8n orquesta.
 */
@RestController
@RequestMapping("/api/bot")
@CrossOrigin(origins = "*")
public class BotReservaController {

    private static final LocalTime APERTURA = LocalTime.of(7, 0);
    private static final LocalTime CIERRE = LocalTime.of(20, 0);
    private static final LocalTime ALMUERZO_INICIO = LocalTime.of(12, 0);
    private static final LocalTime ALMUERZO_FIN = LocalTime.of(14, 0);

    private static final DateTimeFormatter ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter HUMANO = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired private ReservationDao reservationDao;
    @Autowired private ServiceDao serviceDao;
    @Autowired private UserDao userDao;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/pre-reserva")
    @Transactional
    public ResponseEntity<PreReservaResponse> crearPreReserva(@RequestBody PreReservaRequest req) {

        // ---------- 1. Validación de entrada ----------
        if (esVacio(req.getNombrePaciente()) || esVacio(req.getCorreo())
                || esVacio(req.getTelefono()) || esVacio(req.getFechaInicio())
                || esVacio(req.getServicio())) {
            return ResponseEntity.badRequest().body(
                    PreReservaResponse.error("Faltan datos: se requiere nombre, correo, teléfono, servicio y fecha."));
        }

        if (!req.getCorreo().matches("^[^@\\s]+@[^@\\s]+\\.[a-zA-Z]{2,}$")
                || req.getCorreo().toLowerCase().matches(".*(example|ejemplo|test|prueba)\\..*")) {
            return ResponseEntity.badRequest().body(
                    PreReservaResponse.error("El correo no es válido. Pídele al paciente su correo real."));
        }

        LocalDateTime inicio;
        try {
            String limpia = req.getFechaInicio().trim();
            if (limpia.length() > 19) limpia = limpia.substring(0, 19);
            if (limpia.length() == 16) limpia = limpia + ":00";
            inicio = LocalDateTime.parse(limpia, ISO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    PreReservaResponse.error("Fecha inválida. Usa el formato 2026-09-11T16:00:00."));
        }

        if (inicio.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(
                    PreReservaResponse.error("Esa fecha ya pasó. Pídele al paciente una fecha futura."));
        }

        // ---------- 2. Servicio y duración ----------
        ServiceEntity servicio = buscarServicio(req.getServicio());
        if (servicio == null) {
            return ResponseEntity.badRequest().body(
                    PreReservaResponse.error("No reconozco el servicio \"" + req.getServicio() + "\"."));
        }
        int duracion = servicio.getDuracionS();
        LocalDateTime fin = inicio.plusMinutes(duracion);

        // ---------- 3. Horario de atención ----------
        String errorHorario = validarHorario(inicio, fin);
        if (errorHorario != null) {
            PreReservaResponse r = PreReservaResponse.error(errorHorario);
            r.setAlternativas(horariosLibres(inicio, duracion));
            return ResponseEntity.badRequest().body(r);
        }

        // ---------- 4. Disponibilidad ----------
        List<ReservationEntity> choques = reservationDao.findSolapadas(inicio, fin);
        if (!choques.isEmpty()) {
            PreReservaResponse r = new PreReservaResponse();
            r.setDisponible(false);
            r.setEstado("OCUPADO");
            r.setMensaje("Ese horario ya está ocupado.");
            r.setAlternativas(horariosLibres(inicio, duracion));
            return new ResponseEntity<>(r, HttpStatus.CONFLICT);
        }

        // ---------- 5. Paciente ----------
        UserEntity paciente = buscarOCrearPaciente(req);

        // ---------- 6. Guardar ----------
        String sede = deducirSede(inicio);

        ReservationEntity reserva = new ReservationEntity();
        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);
        reserva.setSede(sede);
        reserva.setUsuario(paciente);
        reserva.setServicio(servicio);
        reserva.setEstadoCita("PRE_RESERVA");
        reserva.setPagoConfirmado(false);
        reserva.setEstadoR(true);
        reserva.setGoogleCalendarId(null); // n8n lo envía después con PATCH

        ReservationEntity guardada = reservationDao.save(reserva);

        // ---------- 7. Respuesta ----------
        PreReservaResponse r = new PreReservaResponse();
        r.setDisponible(true);
        r.setReservaId(guardada.getIdReserva());
        r.setEstado("PRE_RESERVA");
        r.setNombrePaciente(paciente.getNombre());
        r.setServicio(servicio.getNombreS());
        r.setSede(sede);
        r.setValor(servicio.getPrecioIndividual());
        r.setFechaInicio(inicio.format(ISO));
        r.setFechaFin(fin.format(ISO));
        r.setDuracionMinutos(duracion);
        r.setMensaje("Espacio apartado para " + paciente.getNombre() + " el "
                + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las "
                + inicio.format(HUMANO) + " en " + sede + ". Valor: $"
                + String.format("%,.0f", servicio.getPrecioIndividual())
                + ". Queda confirmado al recibir el pago.");

        return new ResponseEntity<>(r, HttpStatus.CREATED);
    }

    /** n8n llama esto después de crear el evento en Google Calendar. */
    @PatchMapping("/reserva/{id}/calendar")
    @Transactional
    public ResponseEntity<?> guardarEventoCalendar(@PathVariable Long id,
                                                   @RequestBody java.util.Map<String, String> body) {
        Optional<ReservationEntity> opt = reservationDao.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        ReservationEntity reserva = opt.get();
        reserva.setGoogleCalendarId(body.get("googleCalendarId"));
        reservationDao.save(reserva);
        return ResponseEntity.ok().build();
    }

    /** Lina confirma el pago: la reserva pasa a CONFIRMADA. */
    @PatchMapping("/reserva/{id}/confirmar")
    @Transactional
    public ResponseEntity<PreReservaResponse> confirmarPago(@PathVariable Long id) {
        Optional<ReservationEntity> opt = reservationDao.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        ReservationEntity reserva = opt.get();
        reserva.setPagoConfirmado(true);
        reserva.setEstadoCita("CONFIRMADA");
        reservationDao.save(reserva);

        PreReservaResponse r = new PreReservaResponse();
        r.setDisponible(true);
        r.setReservaId(reserva.getIdReserva());
        r.setEstado("CONFIRMADA");
        r.setNombrePaciente(reserva.getUsuario().getNombre());
        r.setServicio(reserva.getServicio().getNombreS());
        r.setSede(reserva.getSede());
        r.setFechaInicio(reserva.getFechaInicio().format(ISO));
        r.setFechaFin(reserva.getFechaFin().format(ISO));
        r.setMensaje("Cita confirmada.");
        return ResponseEntity.ok(r);
    }

    /** Consulta de agenda por rango, para el bot. */
    @GetMapping("/agenda")
    public ResponseEntity<List<ReservationEntity>> agenda(@RequestParam String desde,
                                                          @RequestParam String hasta) {
        LocalDateTime d = LocalDateTime.parse(desde.length() == 10 ? desde + "T00:00:00" : desde, ISO);
        LocalDateTime h = LocalDateTime.parse(hasta.length() == 10 ? hasta + "T23:59:59" : hasta, ISO);
        return ResponseEntity.ok(reservationDao.findByFechaInicioBetween(d, h));
    }

    // =========================================================
    // Helpers
    // =========================================================

    private boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Tunja de lunes a viernes, Turmequé sábados y domingos. */
    private String deducirSede(LocalDateTime f) {
        DayOfWeek d = f.getDayOfWeek();
        return (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) ? "Turmequé" : "Tunja";
    }

    private String validarHorario(LocalDateTime inicio, LocalDateTime fin) {
        if (!inicio.toLocalDate().equals(fin.toLocalDate()))
            return "La cita no puede cruzar la medianoche.";
        if (inicio.toLocalTime().isBefore(APERTURA))
            return "Atendemos desde las 7:00 a.m.";
        if (fin.toLocalTime().isAfter(CIERRE))
            return "Atendemos hasta las 8:00 p.m. y la cita no alcanzaría a terminar.";
        boolean chocaAlmuerzo = inicio.toLocalTime().isBefore(ALMUERZO_FIN)
                && fin.toLocalTime().isAfter(ALMUERZO_INICIO);
        if (chocaAlmuerzo)
            return "Entre 12:00 p.m. y 2:00 p.m. no se agendan citas.";
        return null;
    }

    /** Devuelve las horas en punto libres del mismo día. */
    private List<String> horariosLibres(LocalDateTime referencia, int duracion) {
        List<String> libres = new ArrayList<>();
        LocalDateTime cursor = referencia.toLocalDate().atTime(APERTURA);

        while (!cursor.toLocalTime().plusMinutes(duracion).isAfter(CIERRE)) {
            LocalDateTime finSlot = cursor.plusMinutes(duracion);
            boolean okHorario = validarHorario(cursor, finSlot) == null;
            boolean okFuturo = cursor.isAfter(LocalDateTime.now());
            boolean okLibre = reservationDao.findSolapadas(cursor, finSlot).isEmpty();

            if (okHorario && okFuturo && okLibre) {
                libres.add(cursor.format(ISO));
                if (libres.size() >= 4) break;
            }
            cursor = cursor.plusMinutes(60);
        }
        return libres;
    }

    /** Busca el servicio por coincidencia parcial de nombre. */
    private ServiceEntity buscarServicio(String texto) {
        String q = normalizar(texto);
        List<ServiceEntity> todos = serviceDao.findAll();

        for (ServiceEntity s : todos) {
            if (normalizar(s.getNombreS()).equals(q)) return s;
        }
        for (ServiceEntity s : todos) {
            String n = normalizar(s.getNombreS());
            if (n.contains(q) || q.contains(n)) return s;
        }
        // Coincidencia por palabra clave
        for (ServiceEntity s : todos) {
            for (String palabra : q.split("\\s+")) {
                if (palabra.length() >= 5 && normalizar(s.getNombreS()).contains(palabra)) return s;
            }
        }
        return null;
    }

    private String normalizar(String s) {
        if (s == null) return "";
        return java.text.Normalizer.normalize(s.toLowerCase().trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    private UserEntity buscarOCrearPaciente(PreReservaRequest req) {
        UserEntity existente = userDao.findByEmail(req.getCorreo());
        if (existente != null) return existente;

        if (!esVacio(req.getDocumento())) {
            Optional<UserEntity> porDoc = userDao.findByDocumento(req.getDocumento());
            if (porDoc.isPresent()) return porDoc.get();
        }

        UserEntity nuevo = new UserEntity();
        nuevo.setNombre(req.getNombrePaciente());
        nuevo.setEmail(req.getCorreo());
        nuevo.setTelefono(req.getTelefono());
        nuevo.setDocumento(req.getDocumento());
        nuevo.setRol(TipoUsuario.CLIENTE);
        //nuevo.setTelegramUserId(req.getTelegramUserId());
        // Clave aleatoria: el paciente creado desde Telegram no inicia sesión todavía
        nuevo.setClave(passwordEncoder.encode(UUID.randomUUID().toString()));
        return userDao.save(nuevo);
    }

    /** Identifica al usuario de Telegram y devuelve su rol. */
    @GetMapping("/usuario/{telegramUserId}")
    public ResponseEntity<java.util.Map<String, Object>> identificarUsuario(
            @PathVariable String telegramUserId) {

        java.util.Map<String, Object> r = new java.util.HashMap<>();
        List<UserEntity> encontrados = userDao.findAllByTelegramUserId(telegramUserId);

        if (encontrados.isEmpty()) {
            r.put("existe", false);
            r.put("rol", "CLIENTE");
            r.put("nombre", null);
            r.put("idUsuario", null);
            return ResponseEntity.ok(r);
        }

        // Si hay varios registros con el mismo Telegram, gana el de mayor privilegio
        UserEntity u = encontrados.stream()
                .filter(x -> x.getRol() == TipoUsuario.ADMIN || x.getRol() == TipoUsuario.FISIO)
                .findFirst()
                .orElse(encontrados.get(0));

        r.put("existe", true);
        r.put("rol", u.getRol().name());
        r.put("nombre", u.getNombre());
        r.put("idUsuario", u.getIdUsuario());
        return ResponseEntity.ok(r);
    }
}
