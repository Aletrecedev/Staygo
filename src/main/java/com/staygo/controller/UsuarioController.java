package com.staygo.controller;

import com.staygo.model.Usuario;
import com.staygo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- REGISTRO ---
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario) {

        // --- ESCUDO QA 1: Validar si el email ya existe ---
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return "redirect:/registro?errorEmail=true";
        }

        // --- ESCUDO QA 2: Validar la fuerza de la contraseña ---
        String pass = usuario.getContrasena();

        // Si la contraseña es nula, menor a 8 caracteres, o le faltan mayúsculas/números
        if (pass == null || pass.length() < 8 || !pass.matches(".*[A-Z].*") || !pass.matches(".*\\d.*")) {
            return "redirect:/registro?errorPassword=true";
        }
        // --------------------------------------------------

        // Encriptar la contraseña si pasa todos los escudos
        String contrasenaEncriptada = passwordEncoder.encode(pass);
        usuario.setContrasena(contrasenaEncriptada);

        // Guardar el usuario
        usuarioRepository.save(usuario);

        // Redirigimos al login
        return "redirect:/login?registroExito=true";
    }

    // --- LOGIN ---
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String contrasena,
                                HttpSession session,
                                Model model) {

        // 1. Buscar si existe alguien con ese email
        Usuario usuario = usuarioRepository.findByEmail(email);

        // 2. Si el usuario existe, comprobar si la contraseña coincide con la encriptada
        if (usuario != null && passwordEncoder.matches(contrasena, usuario.getContrasena())) {

            // Éxito
            session.setAttribute("usuarioLogueado", usuario);
            return "redirect:/";

        } else {
            // Error (O no existe el email, o la contraseña está mal)
            model.addAttribute("error", "Email o contraseña incorrectos. Inténtalo de nuevo.");
            return "login";
        }
    }

    // --- Perfil ---
    @GetMapping("/perfil")
    public String mostrarPerfil(HttpSession session) {
        // Comprobamos si hay alguien logueado
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        // Si no hay sesión, lo mandamos al login por seguridad
        if (usuarioLogueado == null) {
            return "redirect:/login";
        }

        // Si está logueado, le mostramos su pantalla de perfil
        return "perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam("idUsuario") Integer idUsuario,
                                   @RequestParam("nombre") String nombre,
                                   @RequestParam("telefono") String telefono,
                                   @RequestParam(value = "password", required = false) String password,
                                   HttpSession session) {

        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        // Seguridad: verificar que el ID coincide
        if (usuarioLogueado == null || !usuarioLogueado.getIdUsuario().equals(idUsuario)) {
            return "redirect:/login";
        }

        // Actualizamos datos básicos
        usuarioLogueado.setNombre(nombre);
        usuarioLogueado.setTelefono(telefono);

        // Control de contraseña: solo la cambiamos si ha escrito algo nuevo
        if (password != null && !password.trim().isEmpty()) {

            // Opcional: Podrías añadir aquí el mismo escudo de contraseña fuerte si quisieras
            // if (password.length() < 8 ...) { return "redirect:/perfil?errorPassword=true"; }

            usuarioLogueado.setContrasena(passwordEncoder.encode(password));
        }

        // Guardamos los cambios en la base de datos
        usuarioRepository.save(usuarioLogueado);

        // Redirigimos de vuelta al perfil
        return "redirect:/perfil?exito=true";
    }

    // --- LOGOUT ---
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // Mostrar la página Freemium
    @GetMapping("/premium")
    public String mostrarPlanesPremium() {
        return "suscripcion";
    }
}