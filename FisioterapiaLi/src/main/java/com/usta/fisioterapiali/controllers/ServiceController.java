package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.models.Service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "*") // Permite la conexión con el frontend del sitio web
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        List<ServiceEntity> services = serviceService.findAll();
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ServiceEntity>> getActiveServices() {
        List<ServiceEntity> services = serviceService.findActiveServices();
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceEntity> getServiceById(@PathVariable Long id) {
        ServiceEntity service = serviceService.findById(id);
        if (service != null) {
            return new ResponseEntity<>(service, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<?> createService(@RequestBody ServiceEntity service) {
        if (serviceService.existsByNombreS(service.getNombreS())) {
            return new ResponseEntity<>("El servicio ya existe", HttpStatus.BAD_REQUEST);
        }
        ServiceEntity newService = serviceService.save(service);
        return new ResponseEntity<>(newService, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable Long id, @RequestBody ServiceEntity serviceDetails) {
        ServiceEntity existingService = serviceService.findById(id);
        if (existingService == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        existingService.setNombreS(serviceDetails.getNombreS());
        existingService.setDuracionS(serviceDetails.getDuracionS());
        existingService.setEstadoS(serviceDetails.isEstadoS());

        ServiceEntity updatedService = serviceService.update(existingService);
        return new ResponseEntity<>(updatedService, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        ServiceEntity service = serviceService.findById(id);
        if (service == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        serviceService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}