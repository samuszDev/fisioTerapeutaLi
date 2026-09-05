package com.usta.fisioterapiali.components;


import com.usta.fisioterapiali.entities.ServiceEntity;
import com.usta.fisioterapiali.models.Daos.ServiceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class ServicioDataInitializer implements CommandLineRunner {

    @Autowired
    private ServiceDao serviceDao;

    @Override
    public void run(String... args) throws Exception {
        if (serviceDao.count() == 0) {

            // 1. Valoración y Rehabilitación
            ServiceEntity s1 = new ServiceEntity();
            s1.setNombreS("Valoración Inicial");
            s1.setPrecioIndividual(100000.0);
            s1.setDuracionS(60);
            serviceDao.save(s1);

            ServiceEntity s2 = new ServiceEntity();
            s2.setNombreS("Sesión de Rehabilitación / Terapia Física");
            s2.setPrecioIndividual(100000.0);
            s2.setDuracionS(60);
            serviceDao.save(s2);

            // 2. Prescripción de Ejercicio
            ServiceEntity s3 = new ServiceEntity();
            s3.setNombreS("Prescripción de Ejercicio (Individual)");
            s3.setPrecioIndividual(60000.0);
            s3.setDuracionS(60);
            serviceDao.save(s3);

            // 3. Modulación Postejercicio / Descargas Musculares
            ServiceEntity s4 = new ServiceEntity();
            s4.setNombreS("Descarga Muscular - Espalda");
            s4.setPrecioIndividual(70000.0);
            s4.setDuracionS(60);
            serviceDao.save(s4);

            ServiceEntity s5 = new ServiceEntity();
            s5.setNombreS("Descarga Muscular - Miembros Inferiores");
            s5.setPrecioIndividual(90000.0);
            s5.setDuracionS(60);
            serviceDao.save(s5);

            ServiceEntity s6 = new ServiceEntity();
            s6.setNombreS("Descarga Muscular - Cuerpo Completo");
            s6.setPrecioIndividual(150000.0);
            s6.setDuracionS(60);
            serviceDao.save(s6);

            // 4. Procedimientos Especializados
            ServiceEntity s7 = new ServiceEntity();
            s7.setNombreS("Punción Seca (Individual)");
            s7.setPrecioIndividual(120000.0);
            s7.setDuracionS(60);
            serviceDao.save(s7);

            ServiceEntity s8 = new ServiceEntity();
            s8.setNombreS("Terapia Neural (Individual)");
            s8.setPrecioIndividual(150000.0);
            s8.setDuracionS(90);
            serviceDao.save(s8);

            ServiceEntity s9 = new ServiceEntity();
            s9.setNombreS("Plasma Rico en Plaquetas - PRP");
            s9.setPrecioIndividual(250000.0);
            s9.setDuracionS(90);
            serviceDao.save(s9);

            ServiceEntity s10 = new ServiceEntity();
            s10.setNombreS("Sueroterapia");
            s10.setPrecioIndividual(300000.0);
            s10.setDuracionS(90);
            serviceDao.save(s10);

            System.out.println(">>> ¡Servicios oficiales del documento guardados con éxito en la base de datos! <<<");
        }
    }
}