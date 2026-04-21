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

import java.time.LocalDate;
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

        // Usamos el método del repositorio
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
    public String buscarApartamentos(
            @RequestParam(name = "ubicacion", required = false) String ubicacion,
            @RequestParam(name = "fechaInicio", required = false) String fechaInicioStr,
            @RequestParam(name = "fechaFin", required = false) String fechaFinStr,
            @RequestParam(name = "huespedes", required = false) Integer huespedes,
            Model model) {

        // 1. Parseamos las fechas (De String a LocalDate) si el usuario las ha enviado
        LocalDate fechaInicio = (fechaInicioStr != null && !fechaInicioStr.isEmpty())
                ? LocalDate.parse(fechaInicioStr) : null;
        LocalDate fechaFin = (fechaFinStr != null && !fechaFinStr.isEmpty())
                ? LocalDate.parse(fechaFinStr) : null;

        // 2. Ejecutamos nuestra super consulta del repositorio
        List<Apartamento> resultados = apartamentoRepository.buscarDisponibles(ubicacion, fechaInicio, fechaFin, huespedes);

        // 3. Enviamos los resultados a la vista
        model.addAttribute("apartamentos", resultados);

        // 4. Devolvemos los parámetros al Model para que el formulario recuerde la búsqueda
        model.addAttribute("busqueda", ubicacion);
        model.addAttribute("fechaInicio", fechaInicioStr);
        model.addAttribute("fechaFin", fechaFinStr);
        model.addAttribute("huespedes", huespedes);

        return "explorar";
    }

    @GetMapping("/apartamento/{id}")
    public String verDetalleApartamento(@PathVariable Integer id, Model model) {

        Apartamento apartamento = apartamentoService.obtenerPorId(id);

        model.addAttribute("apartamento", apartamento);

        return "detalle_apartamento";
    }
}