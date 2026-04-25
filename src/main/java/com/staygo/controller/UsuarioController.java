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

    // Registro
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute Usuario usuario) {
        // Encriptar la contraseña
        String contrasenaEncriptada = passwordEncoder.encode(usuario.getContrasena());
        usuario.setContrasena(contrasenaEncriptada);

        usuarioRepository.save(usuario);
        return "redirect:/login";
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

    // Mostrar la vista del Perfil
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

    // Procesar los cambios del formulario de perfil
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam("idUsuario") Integer idUsuario,
                                   @RequestParam("nombre") String nombre,
                                   @RequestParam("telefono") String telefono,
                                   @RequestParam(value = "password", required = false) String password,
                                   HttpSession session) {

        // 1. Comprobamos la sesión por seguridad
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        // Medida de seguridad extra: verificar que el ID que llega del formulario
        // es el mismo que el del usuario en sesión (para evitar que alguien modifique otro perfil)
        if (usuarioLogueado == null || !usuarioLogueado.getIdUsuario().equals(idUsuario)) {
            return "redirect:/login";
        }

        // 2. Actualizamos los datos del objeto (esto actualiza automáticamente la sesión también)
        usuarioLogueado.setNombre(nombre);
        usuarioLogueado.setTelefono(telefono);

        // 3. Control de contraseña: solo la cambiamos si ha escrito algo nuevo
        if (password != null && !password.trim().isEmpty()) {
            // IMPORTANTE: Encriptamos la nueva contraseña antes de guardarla para que el login siga funcionando
            usuarioLogueado.setContrasena(passwordEncoder.encode(password));
        }

        // 4. Guardamos los cambios en la base de datos
        usuarioRepository.save(usuarioLogueado);

        // 5. Redirigimos de vuelta al perfil activando la alerta de éxito (?exito=true)
        return "redirect:/perfil?exito=true";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}