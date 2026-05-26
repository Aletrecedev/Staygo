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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ApartamentoRepository apartamentoRepository;

    @PostMapping("/reservar")
    public String hacerReserva(@RequestParam(name = "idApartamento") Integer idApartamento,
                               @RequestParam(name = "fechaInicio", required = false) String fechaInicio,
                               @RequestParam(name = "fechaFin", required = false) String fechaFin,
                               HttpSession session) {

        // 1. Comprobar que el usuario haya iniciado sesión
        Usuario clienteLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (clienteLogueado == null) {
            return "redirect:/login";
        }

        // seguridad
        // Si las fechas vienen nulas o vacías (""), abortamos la misión ANTES de que Java explote
        if (fechaInicio == null || fechaInicio.trim().isEmpty() ||
                fechaFin == null || fechaFin.trim().isEmpty()) {

            // Lo devolvemos a la vista del apartamento (o a explorar) con un parámetro de error
            return "redirect:/apartamento/" + idApartamento + "?errorFechas=true";
        }
        // ---------------------------------------------------

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

        // Cálculo del precio: (Días de estancia * Precio por noche)
        long noches = java.time.temporal.ChronoUnit.DAYS.between(nuevaReserva.getFechaInicio(), nuevaReserva.getFechaFin());

        // Si por algún fallo llegan 0 noches, cobramos mínimo 1 para evitar errores
        if (noches <= 0) noches = 1;

        nuevaReserva.setPrecioTotal(apartamento.getPrecioNoche() * noches);

        // --- Escudo ANTI-OVERBOOKING  ---

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

    // @PostMapping porque es una acción destructiva/modificadora
    // @Transactional para asegurar la integridad (si falla la BD, se hace rollback)
    @PostMapping("/reservas/cancelar/{id}")
    @Transactional
    public String cancelarReserva(@PathVariable Integer id, HttpSession session) {

        // 1. Verificar quién está logueado
        Usuario clienteLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (clienteLogueado == null || !clienteLogueado.getRol().equals("CLIENTE")) {
            return "redirect:/login";
        }

        // 2. Buscar la reserva en la base de datos
        Reserva reserva = reservaRepository.findById(id).orElse(null);

        // 3. SEGURIDAD: Comprobar que la reserva existe y que el dueño es el usuario actual
        if (reserva != null && reserva.getCliente().getIdUsuario().equals(clienteLogueado.getIdUsuario())) {

            // 4. SEGURIDAD Y NEGOCIO (Time-boxing): ¿Faltan menos de 48h (2 días)?
            LocalDate hoy = LocalDate.now();
            long diasHastaCheckin = ChronoUnit.DAYS.between(hoy, reserva.getFechaInicio());

            if (diasHastaCheckin <= 2) {
                // Faltan 2 días o menos, o el viaje ya empezó/pasó. Bloqueamos.
                return "redirect:/mis-viajes?error_tiempo=true";
            }

            // 5. UPDATE en vez de DELETE: Cambiamos el estado para no destruir la contabilidad
            reserva.setEstado("CANCELADA");
            reservaRepository.save(reserva);

            // Redirigimos con la bandera de éxito
            return "redirect:/mis-viajes?cancelada=true";
        }

        // Si intenta cancelar algo que no es suyo o manipuló el ID del formulario
        return "redirect:/mis-viajes?error=true";
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