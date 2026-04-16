package com.staygo.controller;

import com.staygo.model.Apartamento;
import com.staygo.model.Usuario;
import com.staygo.repository.ApartamentoRepository;
import com.staygo.service.ApartamentoService;
import jakarta.servlet.http.HttpSession;
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

    // Panel del Propietario: Solo ve SUS propios pisos
    @GetMapping("/apartamentos")
    public String listarApartamentos(Model model, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado == null || !usuarioLogueado.getRol().equals("PROPIETARIO")) {
            return "redirect:/login";
        }

        // Usamos el nuevo método moderno del repositorio
        List<Apartamento> lista = apartamentoRepository.findByPropietario(usuarioLogueado);
        model.addAttribute("apartamentos", lista);

        return "lista_apartamentos";
    }

    @GetMapping("/apartamentos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("apartamento", new Apartamento());
        return "formulario_apartamento";
    }

    // Guardar: Asigna el objeto Usuario completo como propietario
    @PostMapping("/apartamentos/guardar")
    public String guardarApartamento(@ModelAttribute Apartamento apartamento, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuarioLogueado != null) {
            // EFECTO MODERNO: Pasamos el objeto entero, no solo un ID
            apartamento.setPropietario(usuarioLogueado);
            apartamentoService.guardar(apartamento);
        }

        return "redirect:/apartamentos";
    }

    @GetMapping("/apartamentos/borrar/{id}")
    public String borrarApartamento(@PathVariable Integer id) {
        apartamentoService.borrar(id);
        return "redirect:/apartamentos";
    }

    @GetMapping("/apartamentos/editar/{id}")
    public String mostrarFormularioDeEditar(@PathVariable Integer id, Model model) {
        Apartamento apartamento = apartamentoService.obtenerPorId(id);
        model.addAttribute("apartamento", apartamento);
        return "formulario_apartamento";
    }

    // Vistas PÚBLICAS / CLIENTES

    @GetMapping("/explorar")
    public String explorarApartamentos(Model model) {
        List<Apartamento> lista = apartamentoService.obtenerTodos();
        model.addAttribute("apartamentos", lista);
        return "explorar";
    }

    @GetMapping("/buscar")
    public String buscarApartamentos(@RequestParam String ubicacion, Model model) {
        List<Apartamento> resultados = apartamentoRepository.findByDireccionContainingIgnoreCase(ubicacion);
        model.addAttribute("apartamentos", resultados);
        model.addAttribute("busqueda", ubicacion);
        return "explorar";
    }
}