package com.staygo.controller;

import com.staygo.model.Usuario;
import com.staygo.repository.ApartamentoRepository;
import com.staygo.repository.ReservaRepository;
import com.staygo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ApartamentoRepository apartamentoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @GetMapping("/dashboard")
    public String panelAdmin(HttpSession session, Model model) {
        // 1. Proteger la ruta (Solo ADMIN puede entrar)
        Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");
        if (admin == null || !"ADMIN".equals(admin.getRol())) {
            return "redirect:/";
        }

        // 2. Mandar estadísticas generales a la vista
        model.addAttribute("totalUsuarios", usuarioRepository.count());
        model.addAttribute("totalApartamentos", apartamentoRepository.count());
        model.addAttribute("totalReservas", reservaRepository.count());

        // 3. Mandar listas para las tablas
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("apartamentos", apartamentoRepository.findAll());
        model.addAttribute("reservas", reservaRepository.findAll());

        return "admin-dashboard"; // Nombre de la vista HTML
    }

    // ==========================================
    // MÉTODOS DE ACCIÓN PARA LOS BOTONES (POST)
    // ==========================================

    @org.springframework.web.bind.annotation.PostMapping("/usuarios/bloquear/{id}")
    public String bloquearUsuario(@org.springframework.web.bind.annotation.PathVariable Integer id, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");
        if (admin == null || !"ADMIN".equals(admin.getRol())) return "redirect:/";

        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario != null && !"ADMIN".equals(usuario.getRol())) {
            // SI YA ESTÁ BLOQUEADO: Lo desbloqueamos devolviéndole el rol CLIENTE
            if ("BLOQUEADO".equals(usuario.getRol())) {
                usuario.setRol("CLIENTE");
            } else {
                // SI NO: Lo bloqueamos
                usuario.setRol("BLOQUEADO");
            }
            usuarioRepository.save(usuario);
        }
        return "redirect:/admin/dashboard";
    }

    @org.springframework.web.bind.annotation.PostMapping("/apartamentos/ocultar/{id}")
    public String ocultarApartamento(@org.springframework.web.bind.annotation.PathVariable Integer id, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");
        if (admin == null || !"ADMIN".equals(admin.getRol())) return "redirect:/";

        com.staygo.model.Apartamento apt = apartamentoRepository.findById(id).orElse(null);
        if (apt != null) {
            // SI YA ESTÁ OCULTO: Le quitamos la etiqueta para volver a mostrarlo
            if (apt.getNombre().startsWith("[OCULTO] ")) {
                apt.setNombre(apt.getNombre().replace("[OCULTO] ", ""));
            } else {
                // SI NO: Lo ocultamos
                apt.setNombre("[OCULTO] " + apt.getNombre());
            }
            apartamentoRepository.save(apt);
        }
        return "redirect:/admin/dashboard";
    }

    @org.springframework.web.bind.annotation.PostMapping("/reservas/cancelar/{id}")
    public String cancelarReserva(@org.springframework.web.bind.annotation.PathVariable Integer id, HttpSession session) {
        Usuario admin = (Usuario) session.getAttribute("usuarioLogueado");
        if (admin == null || !"ADMIN".equals(admin.getRol())) return "redirect:/";

        com.staygo.model.Reserva reserva = reservaRepository.findById(id).orElse(null);
        if (reserva != null && reserva.isCancelable()) {
            reserva.setEstado("CANCELADA");
            reservaRepository.save(reserva);
        }
        return "redirect:/admin/dashboard";
    }
}