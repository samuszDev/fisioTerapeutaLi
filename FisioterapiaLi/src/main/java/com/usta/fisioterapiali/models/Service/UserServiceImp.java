package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- Importación añadida
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder; // <-- Inyección añadida

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findAll() {
        return userDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public UserEntity save(UserEntity user) {
        // Lógica de encriptación añadida
        if (user.getClave() == null || user.getClave().isEmpty()) {
            // Usar el documento como contraseña por defecto
            user.setClave(passwordEncoder.encode(user.getDocumento()));
        } else if (!user.getClave().startsWith("$2a$")) {
            // Si ya trae una contraseña pero no está encriptada (BCrypt empieza por $2a$)
            user.setClave(passwordEncoder.encode(user.getClave()));
        }

        // Asignar rol por defecto si es nulo
        if (user.getRol() == null) {
            user.setRol(TipoUsuario.CLIENTE);
        }

        return userDao.save(user);
    }

    @Override
    @Transactional
    public UserEntity update(UserEntity user) {
        UserEntity paciente = userDao.findById(user.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado."));

        paciente.setNombre(user.getNombre());
        paciente.setDocumento(user.getDocumento());
        paciente.setTelefono(user.getTelefono());
        paciente.setEmail(user.getEmail());
        paciente.setEps(user.getEps());
        paciente.setGenero(user.getGenero());
        paciente.setContactoEmergencia(user.getContactoEmergencia());

        // Nota: En el update no tocamos el password ni el rol para no sobrescribirlos accidentalmente al editar un perfil.
        return userDao.save(paciente);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findByRol(TipoUsuario rol) {
        return userDao.findByRol(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userDao.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByDocumento(String documento) {
        return userDao.findByDocumento(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocumento(String documento) {
        return userDao.existsByDocumento(documento);
    }
}