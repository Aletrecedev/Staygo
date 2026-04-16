package com.staygo.controller;


import com.staygo.model.Apartamento;
import com.staygo.model.Reserva;
import com.staygo.model.Usuario;
import com.staygo.repository.ApartamentoRepository;
import com.staygo.repository.ReservaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ApartamentoRepository apartamentoRepository;

    @PostMapping("/reservar")
    public String hacerReserva(@RequestParam Integer idApartamento,
                               @RequestParam String fechaInicio,
                               @RequestParam String fechaFin,
                               HttpSession session) {

        // 1. Comprobar que el usuario haya iniciado sesión
        Usuario clienteLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (clienteLogueado == null) {
            return "redirect:/login";
        }

        // 2. Buscar el apartamento que quiere reservar
        Apartamento apartamento = apartamentoRepository.findById(idApartamento).orElse(null);
        if (apartamento == null) {
            return "redirect:/explorar";
        }

        // 3. Crear la Reserva
        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setCliente(clienteLogueado);
        nuevaReserva.setApartamento(apartamento);
        nuevaReserva.setFechaInicio(LocalDate.parse(fechaInicio));
        nuevaReserva.setFechaFin(LocalDate.parse(fechaFin));
        nuevaReserva.setPrecioTotal(apartamento.getPrecioNoche());

        // --- ESCUDO ANTI-OVERBOOKING  ---

        // A. Comprobamos si las fechas tienen lógica (que no salga antes de entrar)
        if (nuevaReserva.getFechaFin().isBefore(nuevaReserva.getFechaInicio()) || nuevaReserva.getFechaFin().isEqual(nuevaReserva.getFechaInicio())) {
            return "redirect:/explorar?errorLogica=true";
        }

        // B. Buscamos si hay choques
        List<Reserva> choques = reservaRepository.buscarOverbooking(
                apartamento, nuevaReserva.getFechaInicio(), nuevaReserva.getFechaFin()
        );

        // C. Si la lista NO está vacía, es que alguien ya está en el piso
        if (!choques.isEmpty()) {
            return "redirect:/explorar?errorFechas=true";
        }
        // ------------------------------------------

        reservaRepository.save(nuevaReserva);
        return "redirect:/explorar?reservaExito=true";
    }

    @GetMapping("/mis-viajes")
    public String verMisViajes(HttpSession session, Model model) {
        // 1. Comprobamos quién está logueado
        Usuario clienteLogueado = (Usuario) session.getAttribute("usuarioLogueado");

        // 2. Seguridad: Si no está logueado o es PROPIETARIO, lo echamos de aquí
        if (clienteLogueado == null || !clienteLogueado.getRol().equals("CLIENTE")) {
            return "redirect:/login";
        }

        // 3. Usamos el método creado en el Repositorio
        List<Reserva> misReservas = reservaRepository.findByCliente(clienteLogueado);

        // 4. Pasamos la lista de reservas a la vista HTML
        model.addAttribute("reservas", misReservas);

        return "mis-viajes"; // Nombre del archivo HTML
    }

    @GetMapping("/reservas-recibidas")
    public String verReservasRecibidas(HttpSession session, Model model) {
        // 1. Comprobamos que sea un PROPIETARIO
        Usuario propietario = (Usuario) session.getAttribute("usuarioLogueado");
        if (propietario == null || !propietario.getRol().equals("PROPIETARIO")) {
            return "redirect:/login";
        }

        // 2. Buscamos todos los pisos de este propietario
        List<Apartamento> misPisos = apartamentoRepository.findByPropietario(propietario);

        // 3. Juntamos todas las reservas de todos sus pisos en una gran lista
        java.util.List<Reserva> todasLasReservas = new java.util.ArrayList<>();
        for (Apartamento piso : misPisos) {
            todasLasReservas.addAll(reservaRepository.findByApartamento(piso));
        }

        // 4. Pasamos los datos a la vista
        model.addAttribute("reservas", todasLasReservas);

        return "reservas-recibidas";
    }
}