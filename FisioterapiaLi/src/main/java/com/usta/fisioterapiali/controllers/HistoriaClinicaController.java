package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.HistoriaClinicaEntity;
import com.usta.fisioterapiali.entities.UserEntity;
import com.usta.fisioterapiali.models.Service.HistoriaClinicaService;
import com.usta.fisioterapiali.models.Service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class HistoriaClinicaController {

    @Autowired
    private HistoriaClinicaService historiaClinicaService;

    @Autowired
    private UserService usuarioService;

    @GetMapping(value = "/historia-clinica/nueva/{idUsuario}")
    public String crearHistoriaClinica(@PathVariable Long idUsuario, Model model, RedirectAttributes redirectAttributes) {
        UserEntity usuario = usuarioService.findById(idUsuario);

        if (usuario == null) {
            redirectAttributes.addFlashAttribute("error", "The requested user does not exist.");
            return "redirect:/";
        }

        if (historiaClinicaService.existsByUsuarioId(idUsuario)) {
            redirectAttributes.addFlashAttribute("error", "This user already has a clinical history.");
            return "redirect:/historia-clinica/usuario/" + idUsuario;
        }

        HistoriaClinicaEntity historiaClinica = new HistoriaClinicaEntity();
        historiaClinica.setUsuario(usuario);

        model.addAttribute("historiaClinica", historiaClinica);
        model.addAttribute("title", "New Clinical History");
        return "historia-clinica-form";
    }

    @PostMapping("/historia-clinica/guardar")
    public String guardarHistoriaClinica(@ModelAttribute("historiaClinica") @Valid HistoriaClinicaEntity historiaClinica,
                                          BindingResult result,
                                          Model model, RedirectAttributes redirectAttributes,
                                          SessionStatus status) {

        if (result.hasErrors()) {
            model.addAttribute("title", "New Clinical History");
            return "historia-clinica-form";
        }

        historiaClinicaService.save(historiaClinica);
        status.setComplete();
        redirectAttributes.addFlashAttribute("success", "Clinical history saved successfully!");
        return "redirect:/historia-clinica/" + historiaClinica.getIdHistoriaClinica();
    }

    @GetMapping("/historia-clinica/{id}")
    public String verHistoriaClinica(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        HistoriaClinicaEntity historiaClinica = historiaClinicaService.findById(id);

        if (historiaClinica == null) {
            redirectAttributes.addFlashAttribute("error", "Clinical history not found.");
            return "redirect:/";
        }

        model.addAttribute("historiaClinica", historiaClinica);
        model.addAttribute("title", "Clinical History Detail");
        return "historia-clinica-detalle";
    }

    @GetMapping("/historia-clinica/usuario/{idUsuario}")
    public String verHistoriaClinicaPorUsuario(@PathVariable Long idUsuario, Model model, RedirectAttributes redirectAttributes) {
        return historiaClinicaService.findByUsuarioId(idUsuario)
                .map(historiaClinica -> {
                    model.addAttribute("historiaClinica", historiaClinica);
                    model.addAttribute("title", "Clinical History Detail");
                    return "historia-clinica-detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "This user does not have a clinical history yet.");
                    return "redirect:/historia-clinica/nueva/" + idUsuario;
                });
    }

    @GetMapping("/historia-clinica/editar/{id}")
    public String editarHistoriaClinica(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        HistoriaClinicaEntity historiaClinica = historiaClinicaService.findById(id);

        if (historiaClinica == null) {
            redirectAttributes.addFlashAttribute("error", "Clinical history not found.");
            return "redirect:/";
        }

        model.addAttribute("historiaClinica", historiaClinica);
        model.addAttribute("title", "Edit Clinical History");
        return "historia-clinica-form";
    }
}
