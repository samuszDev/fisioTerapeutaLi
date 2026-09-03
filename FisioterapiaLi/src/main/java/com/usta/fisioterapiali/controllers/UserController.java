package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.models.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        UserEntity user = userService.findById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        UserEntity newUser = userService.save(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserEntity userDetails) {
        UserEntity existingUser = userService.findById(id);
        if (existingUser == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Actualización de todos los campos definidos en tu Entity
        existingUser.setNombre(userDetails.getNombre());
        existingUser.setClave(userDetails.getClave());
        existingUser.setTelegramUserId(userDetails.getTelegramUserId());
        existingUser.setRol(userDetails.getRol());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setDocumento(userDetails.getDocumento());
        existingUser.setTelefono(userDetails.getTelefono());
        existingUser.setFechaNacimiento(userDetails.getFechaNacimiento());
        existingUser.setGenero(userDetails.getGenero());
        existingUser.setContactoEmergencia(userDetails.getContactoEmergencia());
        existingUser.setReferidoPor(userDetails.getReferidoPor());
        existingUser.setContadorReferidos(userDetails.getContadorReferidos());

        UserEntity updatedUser = userService.update(existingUser);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        UserEntity user = userService.findById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        userService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}