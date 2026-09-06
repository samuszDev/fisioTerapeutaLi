package com.usta.fisioterapiali.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador exclusivo para las vistas del Panel Administrativo.
 * Se asume que Spring Security protegerá todas las rutas bajo "/admin/**"
 */
@Controller
@RequestMapping("/admin") // Esto hace que todas las rutas de este controlador empiecen por /admin
public class AdminController {

    @GetMapping({"/dashboard", ""})
    public String mostrarDashboard() {
        return "Admin/dashboard";
    }

    @GetMapping({"/clients", "/clientes"})
    public String mostrarClientes() {
        return "Admin/clientes";
    }

    @GetMapping({"/schedule", "/agenda"})
    public String mostrarAgenda() {
        return "Admin/agenda_citas";
    }

    @GetMapping({"/services", "/servicios"})
    public String mostrarServicios() {
        return "Admin/servicios";
    }

    @GetMapping({"/reports", "/reportes"})
    public String mostrarReportes() {
        return "Admin/reportes";
    }
}