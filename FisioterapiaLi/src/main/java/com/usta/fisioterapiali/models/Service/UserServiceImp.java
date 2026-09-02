package com.usta.fisioterapiali.models.Service;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserDao userDao;

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
        return userDao.save(user);
    }

    @Override
    @Transactional
    public UserEntity update(UserEntity user) {
        return userDao.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> findByEmail(String email) {
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