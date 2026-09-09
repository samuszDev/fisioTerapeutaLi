package com.usta.fisioterapiali.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PaginasController {

    @GetMapping("/service")
    public String verServicios() {
        return "service";
    }

    @GetMapping("/sobre-nosotros")
    public String verSobreNosotros() {
        return "sobre-nosotros";
    }

    @GetMapping("/contactanos")
    public String verContactanos() {
        return "contactanos";
    }


}