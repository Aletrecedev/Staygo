package com.staygo.controller;

import com.staygo.model.Apartamento;
import com.staygo.service.ApartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ApartamentoService apartamentoService;

    @GetMapping("/")
    public String home(Model model) {
        // Obtenemos todos los apartamentos de la base de datos
        List<Apartamento> todos = apartamentoService.obtenerTodos();

        // Creamos una lista solo para los 4 primeros
        List<Apartamento> destacados;
        if (todos.size() > 4) {
            destacados = todos.subList(0, 4);
        } else {
            destacados = todos;
        }

        // Se los enviamos al HTML
        model.addAttribute("destacados", destacados);

        return "index";
    }
}