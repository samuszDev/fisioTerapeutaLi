package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.dto.PreReservaRequest;
import com.usta.fisioterapiali.dto.PreReservaResponse;
import com.usta.fisioterapiali.entities.ReservationEntity;
import com.usta.fisioterapiali.entities.ScheduleEntity;
import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.ReservationDao;
import com.usta.fisioterapiali.models.Daos.ServiceDao;
import com.usta.fisioterapiali.models.Daos.UserDao;
import com.usta.fisioterapiali.models.Service.ReservationService;
import com.usta.fisioterapiali.models.Service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Autowired private ScheduleService scheduleService;
    @Autowired private ReservationService reservationService;

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
        // Si agenda la administradora, el pago ya se recibió (efectivo o Nequi presencial)
        boolean laAgendaAdmin = esAdministrador(req.getTelegramUserId());

        reserva.setEstadoCita(laAgendaAdmin ? "CONFIRMADA" : "PRE_RESERVA");
        reserva.setPagoConfirmado(laAgendaAdmin);
        reserva.setEstadoR(true);
        reserva.setGoogleCalendarId(null); // n8n lo envía después con PATCH

        ReservationEntity guardada = reservationDao.save(reserva);

        // ---------- 7. Respuesta ----------
        PreReservaResponse r = new PreReservaResponse();
        r.setDisponible(true);
        r.setReservaId(guardada.getIdReserva());
        r.setEstado(laAgendaAdmin ? "CONFIRMADA" : "PRE_RESERVA");
        r.setNombrePaciente(paciente.getNombre());
        r.setServicio(servicio.getNombreS());
        r.setSede(sede);
        r.setValor(servicio.getPrecioIndividual());
        r.setFechaInicio(inicio.format(ISO));
        r.setFechaFin(fin.format(ISO));
        r.setDuracionMinutos(duracion);
        String base = (laAgendaAdmin ? "Cita CONFIRMADA para " : "Espacio apartado para ")
                + paciente.getNombre() + " el "
                + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las "
                + inicio.format(HUMANO) + " en " + sede + ". Valor: $"
                + String.format("%,.0f", servicio.getPrecioIndividual()) + ".";

        r.setMensaje(laAgendaAdmin
                ? base + " Pago recibido."
                : base + " Queda confirmado al recibir el pago.");

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
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PreReservaResponse.error("No existe la reserva " + id + "."));
        }

        ReservationEntity reserva = opt.get();

        if (!reserva.isEstadoR()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(PreReservaResponse.error("Esa reserva está cancelada."));
        }

        // Idempotente: si Lina aprieta el botón dos veces, responde igual sin fallar.
        boolean yaEstaba = Boolean.TRUE.equals(reserva.getPagoConfirmado());
        if (!yaEstaba) {
            reserva.setPagoConfirmado(true);
            reserva.setEstadoCita("CONFIRMADA");
            reservationDao.save(reserva);
        }

        UserEntity p = reserva.getUsuario();
        ServiceEntity s = reserva.getServicio();

        PreReservaResponse r = new PreReservaResponse();
        r.setDisponible(true);
        r.setReservaId(reserva.getIdReserva());
        r.setEstado("CONFIRMADA");
        r.setNombrePaciente(p != null ? p.getNombre() : "");
        r.setCorreo(p != null ? p.getEmail() : null);
        r.setTelefono(p != null ? p.getTelefono() : null);
        r.setTelegramUserId(p != null ? p.getTelegramUserId() : null);
        r.setServicio(s != null ? s.getNombreS() : "");
        r.setValor(s != null ? s.getPrecioIndividual() : null);
        r.setSede(reserva.getSede());
        r.setGoogleCalendarId(reserva.getGoogleCalendarId());
        r.setFechaInicio(reserva.getFechaInicio().format(ISO));
        r.setFechaFin(reserva.getFechaFin().format(ISO));
        r.setDuracionMinutos(s != null ? s.getDuracionS() : null);
        r.setMensaje(yaEstaba
                ? "Esta cita ya estaba confirmada."
                : "Pago confirmado. Cita de " + r.getNombrePaciente() + " el "
                + reserva.getFechaInicio().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las "
                + reserva.getFechaInicio().format(HUMANO) + " en " + reserva.getSede() + ".");

        return ResponseEntity.ok(r);
    }

    /** Lina cancela una cita. La reserva queda inactiva y el horario se libera. */
    @PatchMapping("/reserva/{id}/cancelar")
    @Transactional
    public ResponseEntity<PreReservaResponse> cancelarReserva(@PathVariable Long id) {
        Optional<ReservationEntity> opt = reservationDao.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PreReservaResponse.error("No existe la reserva " + id + "."));
        }

        ReservationEntity reserva = opt.get();

        // Idempotente: cancelar dos veces no falla.
        boolean yaEstaba = !reserva.isEstadoR();
        if (!yaEstaba) {
            reserva.setEstadoR(false);
            reserva.setEstadoCita("CANCELADA");
            reservationDao.save(reserva);
        }

        UserEntity p = reserva.getUsuario();
        ServiceEntity s = reserva.getServicio();

        PreReservaResponse r = new PreReservaResponse();
        r.setDisponible(false);
        r.setReservaId(reserva.getIdReserva());
        r.setEstado("CANCELADA");
        r.setNombrePaciente(p != null ? p.getNombre() : "");
        r.setCorreo(p != null ? p.getEmail() : null);
        r.setTelefono(p != null ? p.getTelefono() : null);
        r.setTelegramUserId(p != null ? p.getTelegramUserId() : null);
        r.setServicio(s != null ? s.getNombreS() : "");
        r.setValor(s != null ? s.getPrecioIndividual() : null);
        r.setSede(reserva.getSede());
        r.setGoogleCalendarId(reserva.getGoogleCalendarId());
        r.setFechaInicio(reserva.getFechaInicio().format(ISO));
        r.setFechaFin(reserva.getFechaFin().format(ISO));
        r.setDuracionMinutos(s != null ? s.getDuracionS() : null);
        r.setMensaje(yaEstaba
                ? "La reserva " + id + " ya estaba cancelada."
                : "Cita #" + id + " de " + r.getNombrePaciente() + " el "
                + reserva.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " a las " + reserva.getFechaInicio().format(HUMANO)
                + " cancelada. El horario quedó libre.");

        return ResponseEntity.ok(r);
    }

    /** Lina mueve una cita a otra fecha y hora. */
    @PatchMapping("/reserva/{id}/reprogramar")
    @Transactional
    public ResponseEntity<PreReservaResponse> reprogramarReserva(
            @PathVariable Long id,
            @RequestParam("fechaInicio") String fechaInicioStr) {

        Optional<ReservationEntity> opt = reservationDao.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(PreReservaResponse.error("No existe la reserva " + id + "."));
        }

        ReservationEntity reserva = opt.get();
        if (!reserva.isEstadoR()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(PreReservaResponse.error("Esa reserva está cancelada. No se puede reprogramar."));
        }

        LocalDateTime inicio;
        try {
            String limpia = fechaInicioStr.trim();
            if (limpia.length() > 19) limpia = limpia.substring(0, 19);
            if (limpia.length() == 16) limpia = limpia + ":00";
            inicio = LocalDateTime.parse(limpia, ISO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    PreReservaResponse.error("Fecha inválida. Usa el formato 2026-09-11T16:00:00."));
        }

        if (inicio.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(
                    PreReservaResponse.error("Esa fecha ya pasó. Dame una fecha futura."));
        }

        ServiceEntity s = reserva.getServicio();
        int duracion = (s != null && s.getDuracionS() != null) ? s.getDuracionS() : 60;
        LocalDateTime fin = inicio.plusMinutes(duracion);

        String errorHorario = validarHorario(inicio, fin);
        if (errorHorario != null) {
            PreReservaResponse r = PreReservaResponse.error(errorHorario);
            r.setAlternativas(horariosLibres(inicio, duracion));
            return ResponseEntity.badRequest().body(r);
        }

        // Solapamiento, ignorando la propia cita
        for (ReservationEntity c : reservationDao.findSolapadas(inicio, fin)) {
            if (!c.getIdReserva().equals(id)) {
                PreReservaResponse r = new PreReservaResponse();
                r.setDisponible(false);
                r.setEstado("OCUPADO");
                r.setMensaje("Ese horario ya está ocupado.");
                r.setAlternativas(horariosLibres(inicio, duracion));
                return new ResponseEntity<>(r, HttpStatus.CONFLICT);
            }
        }

        LocalDateTime anterior = reserva.getFechaInicio();
        String sede = deducirSede(inicio);

        reserva.setFechaInicio(inicio);
        reserva.setFechaFin(fin);
        reserva.setSede(sede);
        reservationDao.save(reserva);

        UserEntity p = reserva.getUsuario();

        PreReservaResponse r = new PreReservaResponse();
        r.setDisponible(true);
        r.setReservaId(reserva.getIdReserva());
        r.setEstado(reserva.getEstadoCita());
        r.setNombrePaciente(p != null ? p.getNombre() : "");
        r.setCorreo(p != null ? p.getEmail() : null);
        r.setTelefono(p != null ? p.getTelefono() : null);
        r.setTelegramUserId(p != null ? p.getTelegramUserId() : null);
        r.setServicio(s != null ? s.getNombreS() : "");
        r.setValor(s != null ? s.getPrecioIndividual() : null);
        r.setSede(sede);
        r.setGoogleCalendarId(reserva.getGoogleCalendarId());
        r.setFechaInicio(inicio.format(ISO));
        r.setFechaFin(fin.format(ISO));
        r.setDuracionMinutos(duracion);
        r.setMensaje("Cita #" + id + " de " + r.getNombrePaciente() + " movida del "
                + anterior.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las "
                + anterior.format(HUMANO) + " al "
                + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las "
                + inicio.format(HUMANO) + " en " + sede + ".");

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
        if (chocaAlmuerzo) {
            long minutos = java.time.Duration.between(inicio, fin).toMinutes();
            if (inicio.toLocalTime().isBefore(ALMUERZO_INICIO)) {
                return "Ese servicio dura " + minutos + " minutos, así que empezando a las "
                        + inicio.format(HUMANO) + " terminaría a las " + fin.format(HUMANO)
                        + ", dentro de la franja de almuerzo (12:00 p.m. a 2:00 p.m.).";
            }
            return "Entre 12:00 p.m. y 2:00 p.m. no se agendan citas.";
        }
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
        String chatId = resolverTelegram(req.getTelegramUserId());

        // 1. Por correo (normalizado)
        String correo = req.getCorreo() == null ? "" : req.getCorreo().trim().toLowerCase();
        if (!correo.isEmpty()) {
            UserEntity porCorreo = userDao.findByEmail(correo);
            if (porCorreo != null) return completarTelegram(porCorreo, chatId);
        }

        // 2. Por documento
        if (!esVacio(req.getDocumento())) {
            Optional<UserEntity> porDoc = userDao.findByDocumento(req.getDocumento().trim());
            if (porDoc.isPresent()) return completarTelegram(porDoc.get(), chatId);
        }

        // 3. Por teléfono (solo dígitos, para que 300 777 8899 y 3007778899 sean el mismo)
        String tel = soloDigitos(req.getTelefono());
        if (tel.length() >= 7) {
            for (UserEntity u : userDao.findAllByTelefono(req.getTelefono().trim())) {
                if (u.getRol() == TipoUsuario.CLIENTE) return completarTelegram(u, chatId);
            }
        }

        // 4. No existe: se crea
        UserEntity nuevo = new UserEntity();
        nuevo.setNombre(req.getNombrePaciente());
        nuevo.setEmail(correo);
        nuevo.setTelefono(req.getTelefono());
        nuevo.setDocumento(req.getDocumento());
        nuevo.setRol(TipoUsuario.CLIENTE);
        nuevo.setTelegramUserId(chatId);
        nuevo.setClave(passwordEncoder.encode(UUID.randomUUID().toString()));
        return userDao.save(nuevo);
    }

    /**
     * Devuelve el chat id solo si no pertenece ya a la administradora o a la
     * fisioterapeuta. Evita que, cuando Lina agenda para un paciente desde su
     * propio chat, las confirmaciones de ese paciente terminen llegándole a ella.
     */
    private String resolverTelegram(String telegramUserId) {
        if (telegramUserId == null || telegramUserId.isBlank()) return null;
        for (UserEntity u : userDao.findAllByTelegramUserId(telegramUserId)) {
            if (u.getRol() == TipoUsuario.ADMIN || u.getRol() == TipoUsuario.FISIO) return null;
        }
        return telegramUserId;
    }

    /** Si el paciente ya existía pero sin chat de Telegram, se lo completamos. */
    private UserEntity completarTelegram(UserEntity u, String telegramUserId) {
        if (telegramUserId != null && esVacio(u.getTelegramUserId())) {
            u.setTelegramUserId(telegramUserId);
            return userDao.save(u);
        }
        return u;
    }

    private String soloDigitos(String s) {
        return s == null ? "" : s.replaceAll("[^0-9]", "");
    }

    /** True si ese chat de Telegram pertenece a la administradora o a la fisioterapeuta. */
    private boolean esAdministrador(String telegramUserId) {
        if (telegramUserId == null || telegramUserId.isBlank()) return false;
        for (UserEntity u : userDao.findAllByTelegramUserId(telegramUserId)) {
            if (u.getRol() == TipoUsuario.ADMIN || u.getRol() == TipoUsuario.FISIO) return true;
        }
        return false;
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
    // =========================================================
    // 1. CONSULTA PARA CLIENTES (Solo horas libres, sin datos ajenos)
    // =========================================================
    @GetMapping("/disponibilidad")
    public ResponseEntity<Map<String, Object>> consultarDisponibilidadCliente(@RequestParam("fecha") String fechaStr) {
        Map<String, Object> respuesta = new HashMap<>();
        LocalDate fecha = LocalDate.parse(fechaStr);
        int diaSemana = fecha.getDayOfWeek().getValue(); // 1 = Lunes ... 7 = Domingo

        Optional<ScheduleEntity> horarioOpt = scheduleService.findByDiaSemana(diaSemana);
        if (horarioOpt.isEmpty()) {
            respuesta.put("fecha", fechaStr);
            respuesta.put("disponibles", new ArrayList<>());
            respuesta.put("mensaje", "No hay atención en este día de la semana.");
            return ResponseEntity.ok(respuesta);
        }

        ScheduleEntity horario = horarioOpt.get();
        LocalTime apertura = horario.getHoraApertura();
        LocalTime cierre = horario.getHoraCierre();
        LocalTime inicioDescanso = horario.getHoraInicioDescanso();
        LocalTime finDescanso = horario.getHoraFinDescanso();

        List<String> todosLosHorarios = new ArrayList<>();
        LocalTime cursor = apertura;
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        while (cursor.isBefore(cierre)) {
            boolean enDescanso = false;
            if (inicioDescanso != null && finDescanso != null) {
                if (!cursor.isBefore(inicioDescanso) && cursor.isBefore(finDescanso)) {
                    enDescanso = true;
                }
            }
            if (!enDescanso) {
                todosLosHorarios.add(cursor.format(formatoHora));
            }
            cursor = cursor.plusHours(1);
        }

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
        List<ReservationEntity> reservasDelDia = reservationService.findByFechaInicioBetween(inicioDia, finDia);

        List<String> horasOcupadas = new ArrayList<>();
        for (ReservationEntity r : reservasDelDia) {
            if (!r.isEstadoR()) continue;               // ignorar canceladas
            if (r.getFechaInicio() != null) {
                horasOcupadas.add(r.getFechaInicio().toLocalTime().format(formatoHora));
            }
        }

        LocalDateTime ahora = LocalDateTime.now();

        List<String> disponibles = new ArrayList<>();
        for (String h : todosLosHorarios) {
            if (horasOcupadas.contains(h)) continue;

            LocalDateTime inicioSlot = fecha.atTime(LocalTime.parse(h));
            if (!inicioSlot.isAfter(ahora)) continue;   // esa hora ya pasó

            disponibles.add(h);
        }

        respuesta.put("fecha", fechaStr);
        respuesta.put("disponibles", disponibles);

        if (disponibles.isEmpty()) {
            respuesta.put("mensaje", fecha.equals(LocalDate.now())
                    ? "Ya no quedan horarios libres para hoy. Dime otra fecha y te digo qué hay disponible."
                    : "No quedan horarios libres el " + fechaStr + ". Dime otra fecha.");
        }

        return ResponseEntity.ok(respuesta);
    }

    // =========================================================
    // 2. CONSULTA PARA ADMINISTRADOR (Toda la agenda con pacientes)
    // =========================================================
    @GetMapping("/agenda-admin")
    public ResponseEntity<List<Map<String, Object>>> consultarAgendaAdmin(@RequestParam("fecha") String fechaStr) {
        LocalDate fecha = LocalDate.parse(fechaStr);
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

        List<ReservationEntity> reservas = reservationService.findByFechaInicioBetween(inicioDia, finDia);
        List<Map<String, Object>> agenda = new ArrayList<>();
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

        for (ReservationEntity r : reservas) {
            if (!r.isEstadoR()) continue;               // ignorar canceladas

            Map<String, Object> item = new HashMap<>();
            item.put("idReserva", r.getIdReserva());
            item.put("hora", r.getFechaInicio() != null ? r.getFechaInicio().toLocalTime().format(formatoHora) : "");

            String nombrePaciente = "Sin asignar";
            if (r.getUsuario() != null) {
                nombrePaciente = r.getUsuario().getNombre() != null ? r.getUsuario().getNombre() : "Usuario #" + r.getUsuario().getIdUsuario();
            }
            item.put("paciente", nombrePaciente);

            String nombreServicio = "General";
            if (r.getServicio() != null && r.getServicio().getNombreS() != null) {
                nombreServicio = r.getServicio().getNombreS();
            }
            item.put("servicio", nombreServicio);

            item.put("estado", r.getEstadoCita() != null ? r.getEstadoCita() : "PENDIENTE");

            agenda.add(item);
        }

        return ResponseEntity.ok(agenda);
    }
    @GetMapping("/reserva/pendiente/{telegramUserId}")
    public ResponseEntity<Map<String, Object>> reservaPendiente(
            @PathVariable String telegramUserId) {

        Map<String, Object> r = new HashMap<>();
        List<UserEntity> usuarios = userDao.findAllByTelegramUserId(telegramUserId);
        if (usuarios.isEmpty()) {
            r.put("existe", false);
            return ResponseEntity.ok(r);
        }

        ReservationEntity pendiente = null;
        for (UserEntity u : usuarios) {
            for (ReservationEntity res : reservationDao.findByUsuario_IdUsuario(u.getIdUsuario())) {
                if (!res.isEstadoR()) continue;
                if (Boolean.TRUE.equals(res.getPagoConfirmado())) continue;
                if (pendiente == null || res.getFechaInicio().isAfter(pendiente.getFechaInicio())) {
                    pendiente = res;
                }
            }
        }

        if (pendiente == null) {
            r.put("existe", false);
            return ResponseEntity.ok(r);
        }

        r.put("existe", true);
        r.put("reservaId", pendiente.getIdReserva());
        r.put("nombrePaciente", pendiente.getUsuario().getNombre());
        r.put("servicio", pendiente.getServicio() != null
                ? pendiente.getServicio().getNombreS() : "");
        r.put("valor", pendiente.getServicio() != null
                ? pendiente.getServicio().getPrecioIndividual() : null);
        r.put("sede", pendiente.getSede());
        r.put("fechaInicio", pendiente.getFechaInicio().format(ISO));
        r.put("googleCalendarId", pendiente.getGoogleCalendarId());
        return ResponseEntity.ok(r);
    }
}