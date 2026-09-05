package com.usta.fisioterapiali.components;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Daos.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserDao userDao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userDao.count() == 0) {


            UserEntity admin = new UserEntity();
            admin.setDocumento("1001234567");
            admin.setNombre("Carlos Admin");
            admin.setEmail("admin@fisioterapiali.com");
            admin.setClave(passwordEncoder.encode("admin123"));
            admin.setRol(TipoUsuario.ADMIN);
            admin.setTelegramUserId("1");
            userDao.save(admin);

            UserEntity fisio = new UserEntity();
            fisio.setDocumento("1009876543");
            fisio.setNombre("Dra. Sofia Fisioterapeuta");
            fisio.setEmail("fisio@fisioterapiali.com");
            fisio.setClave(passwordEncoder.encode("fisio123"));
            fisio.setRol(TipoUsuario.FISIO);
            fisio.setTelegramUserId("2");
            userDao.save(fisio);


            UserEntity cliente = new UserEntity();
            cliente.setDocumento("1005554433");
            cliente.setNombre("Juan Paciente");
            cliente.setEmail("usuario@fisioterapiali.com");
            cliente.setClave(passwordEncoder.encode("user123"));
            cliente.setRol(TipoUsuario.CLIENTE);
            cliente.setTelegramUserId("3");
            userDao.save(cliente);

            System.out.println(">>> ¡Usuarios quemados con éxito y contraseñas cifradas en la base de datos! <<<");
        }
    }
}