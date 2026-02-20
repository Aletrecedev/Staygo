package com.staygo.controller;

import com.staygo.model.Apartamento;
import com.staygo.service.ApartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;

@Controller
public class ApartamentoController {

    @Autowired
    private ApartamentoService apartamentoService;

    @GetMapping("/apartamentos")
    public String listarApartamentos(Model model) {
        // Lista de apartamentos
        List<Apartamento> lista = apartamentoService.obtenerTodos();

        // Se la pasamos al HTML bajo el nombre "apartamentos"
        model.addAttribute("apartamentos", lista);

        // Devolvemos el nombre del archivo HTML
        return "lista_apartamentos";
    }

    // Mostrar el formulario vacío
    @GetMapping("/apartamentos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("apartamento", new Apartamento());
        return "formulario_apartamento";
    }

    // Recibir los datos del formulario y guardarlos
    @PostMapping("/apartamentos/guardar")
    public String guardarApartamento(@ModelAttribute Apartamento apartamento) {
        // Asignar el apartamento temporalmente a id1 para que no dé error.
        apartamento.setIdPropietario(1);
        apartamentoService.guardar(apartamento);
        return "redirect:/apartamentos";
    }
}