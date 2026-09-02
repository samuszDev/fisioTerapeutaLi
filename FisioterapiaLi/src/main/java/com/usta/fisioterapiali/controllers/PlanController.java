package com.usta.fisioterapiali.controllers;

import com.usta.fisioterapiali.entities.PlanEntity;
import com.usta.fisioterapiali.models.Service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
@CrossOrigin(origins = "*")
public class PlanController {

    @Autowired
    private PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanEntity>> getAllPlans() {
        List<PlanEntity> plans = planService.findAll();
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanEntity> getPlanById(@PathVariable Long id) {
        PlanEntity plan = planService.findById(id);
        if (plan != null) {
            return new ResponseEntity<>(plan, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/servicio/{idServicio}")
    public ResponseEntity<List<PlanEntity>> getPlansByService(@PathVariable Long idServicio) {
        List<PlanEntity> plans = planService.findByServicioId(idServicio);
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/grupales")
    public ResponseEntity<List<PlanEntity>> getGrupales() {
        List<PlanEntity> plans = planService.findGrupales();
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/individuales")
    public ResponseEntity<List<PlanEntity>> getIndividuales() {
        List<PlanEntity> plans = planService.findIndividuales();
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PlanEntity> createPlan(@RequestBody PlanEntity plan) {
        PlanEntity newPlan = planService.save(plan);
        return new ResponseEntity<>(newPlan, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanEntity> updatePlan(@PathVariable Long id, @RequestBody PlanEntity planDetails) {
        PlanEntity existingPlan = planService.findById(id);
        if (existingPlan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        existingPlan.setNombrePlan(planDetails.getNombrePlan());
        existingPlan.setCantidadSesiones(planDetails.getCantidadSesiones());
        existingPlan.setPrecioTotal(planDetails.getPrecioTotal());
        existingPlan.setEsGrupal(planDetails.getEsGrupal());
        existingPlan.setServicio(planDetails.getServicio());

        PlanEntity updatedPlan = planService.update(existingPlan);
        return new ResponseEntity<>(updatedPlan, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        PlanEntity plan = planService.findById(id);
        if (plan == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        planService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}