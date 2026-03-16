package com.staygo.controller;

import com.staygo.model.Apartamento;
import com.staygo.model.Reserva;
import com.staygo.model.Usuario;
import com.staygo.repository.ApartamentoRepository;
import com.staygo.repository.ReservaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

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
            return "redirect:/login"; // Si no está logueado, lo mandamos al login
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

        // Calcula un precio total básico (Precio por noche * 1 noche por ahora para simplificar)
        nuevaReserva.setPrecioTotal(apartamento.getPrecioNoche());

        // 4. Guardar en la base de datos
        reservaRepository.save(nuevaReserva);

        // 5. Redirigimos a la página principal
        return "redirect:/explorar?reservaExito=true";
    }
}