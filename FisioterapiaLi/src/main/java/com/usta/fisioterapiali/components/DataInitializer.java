package com.usta.fisioterapiali.components;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserDao userDao;

    @Override
    public void run(String... args) throws Exception {
        if (userDao.count() == 0) {

            // 1. Administrador
            UserEntity admin = new UserEntity();
            admin.setNombre("Carlos Admin");
            admin.setEmail("admin@fisioterapiali.com");
            admin.setClave("admin123");
            admin.setRol(TipoUsuario.ADMIN);
            admin.setTelegramUserId("1");
            userDao.save(admin);

            // 2. Fisioterapeuta
            UserEntity fisio = new UserEntity();
            fisio.setNombre("Dra. Sofia Fisioterapeuta");
            fisio.setEmail("fisio@fisioterapiali.com");
            fisio.setClave("fisio123");
            fisio.setRol(TipoUsuario.FISIO);
            fisio.setTelegramUserId("2");
            userDao.save(fisio);

            // 3. Usuario / Cliente
            UserEntity cliente = new UserEntity();
            cliente.setNombre("Juan Paciente");
            cliente.setEmail("usuario@fisioterapiali.com");
            cliente.setClave("user123");
            cliente.setRol(TipoUsuario.CLIENTE);
            cliente.setTelegramUserId("3");
            userDao.save(cliente);

            System.out.println(">>> ¡Usuarios quemados con éxito en la base de datos! <<<");
        }
    }
}