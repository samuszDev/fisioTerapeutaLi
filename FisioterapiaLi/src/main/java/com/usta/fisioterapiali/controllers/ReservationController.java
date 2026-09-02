package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.ReservationEntity;
import com.usta.fisioterapiali.models.Service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<ReservationEntity>> getAllReservations() {
        List<ReservationEntity> reservations = reservationService.findAllByOrderByFechaInicioAsc();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationEntity> getReservationById(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.findById(id);
        if (reservation != null) {
            return new ResponseEntity<>(reservation, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ReservationEntity>> getReservationsByUser(@PathVariable Long idUsuario) {
        List<ReservationEntity> reservations = reservationService.findByUsuarioId(idUsuario);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<List<ReservationEntity>> getReservationsByService(@PathVariable Long idServicio) {
        List<ReservationEntity> reservations = reservationService.findByServicioId(idServicio);
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/google/{googleCalendarId}")
    public ResponseEntity<ReservationEntity> getByGoogleCalendarId(@PathVariable String googleCalendarId) {
        Optional<ReservationEntity> reservation = reservationService.findByGoogleCalendarId(googleCalendarId);
        if (reservation.isPresent()) {
            return new ResponseEntity<>(reservation.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<ReservationEntity> createReservation(@RequestBody ReservationEntity reservation) {
        ReservationEntity newReservation = reservationService.save(reservation);
        return new ResponseEntity<>(newReservation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationEntity> updateReservation(@PathVariable Long id, @RequestBody ReservationEntity reservationDetails) {
        ReservationEntity existingReservation = reservationService.findById(id);
        if (existingReservation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        existingReservation.setFechaInicio(reservationDetails.getFechaInicio());
        existingReservation.setFechaFin(reservationDetails.getFechaFin());
        existingReservation.setGoogleCalendarId(reservationDetails.getGoogleCalendarId());
        existingReservation.setSede(reservationDetails.getSede());
        existingReservation.setPagoConfirmado(reservationDetails.getPagoConfirmado());
        existingReservation.setMetodoPago(reservationDetails.getMetodoPago());
        existingReservation.setEstadoR(reservationDetails.isEstadoR());
        existingReservation.setUsuario(reservationDetails.getUsuario());
        existingReservation.setServicio(reservationDetails.getServicio());

        ReservationEntity updatedReservation = reservationService.update(existingReservation);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        ReservationEntity reservation = reservationService.findById(id);
        if (reservation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        reservationService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}