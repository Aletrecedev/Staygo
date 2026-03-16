package com.staygo.controller;

import com.staygo.model.Apartamento;
import com.staygo.repository.ApartamentoRepository;
import com.staygo.service.ApartamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ApartamentoController {

    @Autowired
    private ApartamentoService apartamentoService;
    @Autowired
    private ApartamentoRepository apartamentoRepository;

    @GetMapping("/apartamentos")
    public String listarApartamentos(Model model) {
        List<Apartamento> lista = apartamentoService.obtenerTodos();

        model.addAttribute("apartamentos", lista);

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

    //Para borrar el apartamento por su ID
    @GetMapping("/apartamentos/borrar/{id}")
    public String borrarApartamento(@PathVariable Integer id) {
        apartamentoService.borrar(id);
        return "redirect:/apartamentos";
    }

    // Mostrar el formulario pero con los datos prerellenados
    @GetMapping("/apartamentos/editar/{id}")
    public String mostrarFormularioDeEditar(@PathVariable Integer id, Model model) {
        Apartamento apartamento = apartamentoService.obtenerPorId(id);
        model.addAttribute("apartamento", apartamento);
        return "formulario_apartamento";
    }

    // Vista para clientes
    @GetMapping("/explorar")
    public String explorarApartamentos(Model model) {
        List<Apartamento> lista = apartamentoService.obtenerTodos();
        model.addAttribute("apartamentos", lista);
        return "explorar";
    }

    // Buscador
    @GetMapping("/buscar")
    public String buscarApartamentos(@RequestParam String ubicacion, Model model) {
        // Buscar en la base de datos
        List<Apartamento> resultados = apartamentoRepository.findByDireccionContainingIgnoreCase(ubicacion);

        // Pasar los resultados a la vista
        model.addAttribute("apartamentos", resultados);

        // Pasar también la palabra que buscaron para poder poner un título como "Resultados para: Madrid"
        model.addAttribute("busqueda", ubicacion);

        return "explorar";
    }
}