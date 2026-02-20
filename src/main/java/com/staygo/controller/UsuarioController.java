package com.staygo.controller;

import com.staygo.model.Usuario;
import com.staygo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario) {
        usuarioRepository.save(usuario);
        return "redirect:/login";
    }

    // Login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    // Procesar los datos del login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String contrasena,
                                HttpSession session,
                                Model model) {

        Usuario usuario = usuarioRepository.findByEmailAndContrasena(email, contrasena);

        if (usuario != null) {
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/explorar";
        } else {
            model.addAttribute("error", "Email o contraseña incorrectos. Inténtalo de nuevo.");
            return "login";
        }
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}