package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.ServiceDao;
import com.usta.fisioterapiali.models.Daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modal")
public class ApiController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ServiceDao serviceDao;

    @GetMapping("/pacientes")
    public List<UserEntity> obtenerPacientes() {
        return userDao.findAll();
    }

    @GetMapping("/servicios")
    public List<ServiceEntity> obtenerServicios() {
        return serviceDao.findAll();
    }

    // MÉTODO PARA CREAR / GUARDAR UN NUEVO PACIENTE EN LA BASE DE DATOS
    @PostMapping("/pacientes")
    public ResponseEntity<?> guardarPaciente(@RequestBody UserEntity cliente) {
        try {
            // Asignar rol por defecto si viene nulo
            if (cliente.getRol() == null) {
                cliente.setRol(TipoUsuario.CLIENTE); // O el enum correspondiente que uses
            }

            // Asignar contraseña temporal por defecto para cumplir con la restricción @NotBlank
            if (cliente.getClave() == null || cliente.getClave().isBlank()) {
                cliente.setClave("123456");
            }

            // Inicializar contador si viene nulo
            if (cliente.getContadorReferidos() == null) {
                cliente.setContadorReferidos(0);
            }

            UserEntity nuevoCliente = userDao.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el paciente: " + e.getMessage());
        }
    }
}