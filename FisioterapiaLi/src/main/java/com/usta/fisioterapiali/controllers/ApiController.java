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
import java.util.Optional;

@RestController
@RequestMapping("/api/modal")
public class ApiController {

    @Autowired
    private UserDao userDao;

    @Autowired
    private ServiceDao serviceDao;



    @GetMapping("/servicios")
    public List<ServiceEntity> obtenerServicios() {
        return serviceDao.findAll();
    }
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
                    return ResponseEntity
                            .status(HttpStatus.BAD_REQUEST)
                            .body("El número de documento ya se encuentra registrado.");
                }
            }

            if (cliente.getRol() == null) {
                cliente.setRol(TipoUsuario.CLIENTE);
            }
            if (cliente.getClave() == null || cliente.getClave().isBlank()) {
                cliente.setClave("123456");
            }
            if (cliente.getContadorReferidos() == null) {
                cliente.setContadorReferidos(0);
            }

            UserEntity nuevoCliente = userDao.save(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la solicitud: " + e.getMessage());
        }
    }
}