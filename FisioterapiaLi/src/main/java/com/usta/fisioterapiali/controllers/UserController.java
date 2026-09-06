package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.enums.TipoUsuario;
import com.usta.fisioterapiali.models.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class UserController {

    @Autowired
    private UserService usuarioService;

    @GetMapping(value = "/register")
    public String crearUsuario(Model model) {
        model.addAttribute("usuario", new UserEntity());
        model.addAttribute("title", "Register a new user");
        return "register";
    }

    @PostMapping("/register")
    public String registro(@ModelAttribute("usuario") @Valid UserEntity usuario,
                           BindingResult result,
                           @RequestParam("confirmarClave") String confirmarClave,
                           Model model, RedirectAttributes redirectAttributes,
                           SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("title", "Register a new user");
            return "register";
        }

        if (!usuario.getClave().equals(confirmarClave)) {
            result.rejectValue("clave", "error.usuario", "The passwords do not match.");
            model.addAttribute("title", "Register a new User");
            return "register";
        }

        String pass = new BCryptPasswordEncoder().encode(usuario.getClave());
        usuario.setClave(pass);

        // Asignación correcta del rol usando el Enum TipoUsuario
        usuario.setRol(TipoUsuario.CLIENTE);

        usuarioService.save(usuario);
        status.setComplete();
        redirectAttributes.addFlashAttribute("success", "User registered successfully!");
        return "redirect:/login";
    }
}